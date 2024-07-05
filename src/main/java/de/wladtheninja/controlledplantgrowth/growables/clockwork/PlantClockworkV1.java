package de.wladtheninja.controlledplantgrowth.growables.clockwork;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.PlantConceptManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthInformation;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlantClockworkV1 implements IPlantClockwork {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> scheduledFuture;

    public boolean queueNextBlockToUpdateCheckPast(long currentTime) {
        List<PlantBaseBlockDTO> oldPlant = PlantBaseBlockDAO.getInstance().getPastUpdates(currentTime);
        return growPlants(oldPlant, false);
    }

    public void growPlantsForceSync(List<PlantBaseBlockDTO> plants) {
        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), () -> growPlants(plants, true));
    }

    public boolean growPlants(List<PlantBaseBlockDTO> plants,
                              boolean forceCall) {
        if (plants == null || plants.isEmpty()) {
            return false;
        }

        plants.forEach(this::growPlant);

        if (forceCall) {
            callQueueNextSync();
        }
        return true;
    }

    public boolean queueNextBlockToUpdateCheckFuture(long currentTime) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }

        List<PlantBaseBlockDTO> plants = PlantBaseBlockDAO.getInstance()
                .getNextFutureUpdate(currentTime,
                                     SettingsDAO.getInstance()
                                             .getCurrentSettings()
                                             .getMaximumTimeWindowInMillisecondsForPlantsToBeClustered());

        if (plants == null || plants.isEmpty()) {
            return false;
        }

        scheduledFuture = scheduledExecutorService.schedule(() -> {
            growPlantsForceSync(plants);
        }, Math.max(plants.getFirst().getTimeNextGrowthStage() - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
        return true;
    }

    public void callQueueNextSync() {
        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), this::queueNextBlockToUpdate);
    }

    @Override
    public void queueNextBlockToUpdate() {
        long currentTime = System.currentTimeMillis();

        boolean updatedPast = queueNextBlockToUpdateCheckPast(currentTime);
        boolean updatedFuture = queueNextBlockToUpdateCheckFuture(currentTime);

        if (!updatedPast || updatedFuture) {
            return;
        }

        callQueueNextSync();
    }

    public void growPlantForceSync(PlantBaseBlockDTO plant) {
        if (plant == null) {
            return;
        }

        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), () -> growPlant(plant));
    }

    public void growPlant(PlantBaseBlockDTO plant) {
        if (plant == null) {
            return;
        }

        IPlantConcept ipc =
                PlantConceptManager.getInstance().retrieveSuitedPlantConcept(plant.getLocation().getBlock().getType());

        if (ipc == null) {
            PlantBaseBlockDAO.getInstance().deletePlantBase(plant.getLocation().getBlock());
            return;
        }

        // TODO add error msg on fail ... should never fail though
        IPlantConceptGrowthInformation conceptGrowthInformation = (IPlantConceptGrowthInformation) ipc;

        conceptGrowthInformation.increaseGrowthStep(plant.getLocation().getBlock());

        if (conceptGrowthInformation.isMature(plant.getLocation().getBlock())) {
            PlantBaseBlockDAO.getInstance().deletePlantBase(plant.getLocation().getBlock());
            return;
        }

        transferMCPlantAgeToDTO(ipc, plant);
        PlantBaseBlockDAO.getInstance().updatePlantBaseBlockDTO(plant);

    }

    public void transferMCPlantAgeToDTO(IPlantConcept ipc,
                                        PlantBaseBlockDTO pbb) {
        IPlantConceptGrowthInformation conceptGrowthInformation = ipc instanceof IPlantConceptGrowthInformation ?
                (IPlantConceptGrowthInformation) ipc :
                null;

        IPlantConceptAge conceptAge = ipc instanceof IPlantConceptAge ?
                (IPlantConceptAge) ipc :
                null;

        if (conceptGrowthInformation == null) {
            throw new RuntimeException("Plant does not have any levels of age ...");
        }

        if (conceptGrowthInformation.isMature(pbb.getLocation().getBlock())) {
            throw new RuntimeException("Planted mature plant :)");
        }

        SettingsPlantGrowthDTO settingsPlantGrowthDTO =
                SettingsDAO.getInstance().getPlantSettings(pbb.getLocation().getBlock().getType());

        pbb.setCurrentPlantStage(0);
        double nextStepTimeInMs = 0;

        if (conceptAge != null) {
            pbb.setCurrentPlantStage(conceptAge.getCurrentAge(pbb.getLocation().getBlock()));
            nextStepTimeInMs = (((double) settingsPlantGrowthDTO.getTimeForPlantMature()) /
                    conceptAge.getMaximumAge(pbb.getLocation().getBlock()));
        }
        else {
            nextStepTimeInMs = settingsPlantGrowthDTO.getTimeForPlantMature();
        }

        pbb.setTimeNextGrowthStage(System.currentTimeMillis() + (long) (nextStepTimeInMs * 1000));
    }

    @Override
    public void onPreSaveNewPlantBaseBlockEvent(IPlantConcept ipc,
                                                PlantBaseBlockDTO pbb) {
        transferMCPlantAgeToDTO(ipc, pbb);
    }

    @Override
    public void onBreakPlant(IPlantConcept ipc,
                             PlantBaseBlockDTO pbb) {

    }

    @Override
    public void onAfterSaveNewPlantEvent() {
        queueNextBlockToUpdate();
    }
}
