package de.wladtheninja.controlledplantgrowth.growables.clockwork;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.PlantConceptManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthInformation;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlantClockworkV1 implements IPlantClockwork {

    @Getter(lazy = true)
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private PlantBaseBlockDTO currentScope = null;
    private ScheduledFuture<?> future;

    public boolean checkNextBlockPastUpdates(long currentTime) {
        List<PlantBaseBlockDTO> pastPbb = PlantBaseBlockDAO.getInstance().getPastUpdates(currentTime);

        if (pastPbb == null || pastPbb.isEmpty()) {
            return false;
        }

        getScheduledExecutorService().execute(() -> {
            Bukkit.getLogger().log(Level.FINER, "Block update logic would be here");

            for (PlantBaseBlockDTO pbb : pastPbb) {
                try {
                    checkNextBlockGrowthInnerLogic(pbb);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        return true;

    }

    private boolean checkNextBlockGrowthInnerLogic(PlantBaseBlockDTO pbb) {
        IPlantConcept ipc =
                PlantConceptManager.getInstance().retrieveSuitedPlantConcept(pbb.getLocation().getBlock().getType());

        if (ipc == null) {
            PlantBaseBlockDAO.getInstance().deletePlantBase(pbb.getLocation().getBlock());
            return true;
        }

        Bukkit.getServer()
                .getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                         () -> onUpdateGrowthEvent(ipc, pbb));
        return false;
    }

    public boolean checkNextBlockFutureUpdates(long currentTime) {
        PlantBaseBlockDTO pbb = PlantBaseBlockDAO.getInstance().getNextFutureUpdate(currentTime);

        if (pbb == null) {
            return false;
        }

        if (currentScope != null && currentScope.getTimeNextGrowthStage() < pbb.getTimeNextGrowthStage()) {
            return false;
        }

        if (currentScope != null) {
            future.cancel(false);
        }

        currentScope = pbb;

        Bukkit.getLogger()
                .log(Level.FINER,
                     "Found next block to update in " + (currentTime - currentScope.getTimeNextGrowthStage()) + "ms: ");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Bukkit.getLogger().log(Level.FINER, gson.toJson(pbb));

        future = getScheduledExecutorService().schedule(() -> {
            Bukkit.getLogger().log(Level.FINER, "Block update logic would be here");

            try {
                checkNextBlockGrowthInnerLogic(pbb);
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                currentScope = null;
                future = null;
            }
        }, Math.max(currentScope.getTimeNextGrowthStage() - currentTime, 200), TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void queueNextBlockToUpdate() {
        long currentTime = Instant.now().toEpochMilli();

        boolean entries = checkNextBlockPastUpdates(currentTime);
        boolean entries2 = checkNextBlockFutureUpdates(currentTime);

        if(entries || entries2)
            queueNextBlockToUpdate();
    }

    public void onUpdateGrowthEvent(IPlantConcept ipc,
                                    PlantBaseBlockDTO pbb) {
        // TODO Check if chunk is loaded and act accordingly ... necessary distinction? Test
        /* if (!pbb.getLocation().getBlock().getChunk().isLoaded()) {
            pbb.setMarkedAsOfflineChunk(true);
            updateGrowthInformation(ipc, pbb, true);
            return;
        } */

        /* if(!pbb.getLocation().getBlock().getChunk().isLoaded()) {
            pbb.getLocation().getBlock().getChunk().load();
        } */

        if (ipc instanceof IPlantConceptAge) {
            IPlantConceptAge age = (IPlantConceptAge) ipc;

            age.setCurrentAge(pbb.getLocation().getBlock(),
                              Math.min(age.getCurrentAge(pbb.getLocation().getBlock()) + 1,
                                       age.getMaximumAge(pbb.getLocation().getBlock())));
        }
        else if (ipc instanceof IPlantConceptGrowthInformation) {
            IPlantConceptGrowthInformation growthInformation = (IPlantConceptGrowthInformation) ipc;
            growthInformation.setToFullyMature(pbb.getLocation().getBlock());
        }

        if (updateGrowthInformation(ipc, pbb)) {
            PlantBaseBlockDAO.getInstance().updatePlantBaseBlockDTO(pbb);
        }
    }

    @Override
    public void onPreSaveNewPlantBaseBlockEvent(IPlantConcept ipc,
                                                PlantBaseBlockDTO pbb) {
        updateGrowthInformation(ipc, pbb);
    }

    public boolean updateGrowthInformation(IPlantConcept ipc,
                                           PlantBaseBlockDTO pbb) {

        if (!(ipc instanceof IPlantConceptGrowthInformation) ||
                ((IPlantConceptGrowthInformation) ipc).isMature(pbb.getLocation().getBlock())) {
            PlantBaseBlockDAO.getInstance().deletePlantBase(pbb.getLocation().getBlock());
            return false;
        }

        long currentTime = Instant.now().toEpochMilli();

        SettingsPlantGrowthDTO plantSettings =
                SettingsDAO.getInstance().getPlantSettings(pbb.getLocation().getBlock().getType());

        long waitUntilNextUpdate = 0;

        pbb.setCurrentPlantStage(0);

        final IPlantConceptGrowthInformation growthInformation = (IPlantConceptGrowthInformation) ipc;
        IPlantConceptAge age = null;
        if (ipc instanceof IPlantConceptAge) {
            age = (IPlantConceptAge) ipc;
        }

        if (!plantSettings.isUseTimeForPlantMature() && age != null) {
            waitUntilNextUpdate =
                    plantSettings.getTimeForNextPlantGrowthInSteps()[age.getCurrentAge(pbb.getLocation().getBlock())] *
                            1000L;
        }
        else if (plantSettings.isUseTimeForPlantMature()) {
            waitUntilNextUpdate = (plantSettings.getTimeForPlantMature() * 1000) / (age == null ?
                    1 :
                    age.getMaximumAge(pbb.getLocation().getBlock()));
        }

        pbb.setTimeNextGrowthStage(waitUntilNextUpdate + currentTime);

        return true;
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
