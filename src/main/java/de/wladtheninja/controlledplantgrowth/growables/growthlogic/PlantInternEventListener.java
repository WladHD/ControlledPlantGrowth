package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockIdDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthInformation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptLocation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptMultiBlockGrowthVertical;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;

public class PlantInternEventListener implements IPlantInternEventListener {

    HashMap<PlantBaseBlockIdDTO, BukkitTask> delayedTasks = new HashMap<>();

    @Override

    public void onUnexpectedRegisteredPlantPlayerPlaceEvent(IPlantConcept ipc,
                                                            PlantBaseBlockDTO pbb) {

        onPlantPlayerRescanEventAsync(pbb);
    }

    @Override
    public void onUnexpectedUnregisteredPlantPlayerPlaceEvent(IPlantConcept ignored,
                                                              PlantBaseBlockDTO pbb) {

        onPlantPlayerRescanEventAsync(pbb);
    }

    public void onPlantPlayerRescanEventAsync(PlantBaseBlockDTO pbb) {
        if (delayedTasks.containsKey(pbb.getPlantBaseBlockIdDTO())) {
            delayedTasks.get(pbb.getPlantBaseBlockIdDTO()).cancel();
        }

        delayedTasks.put(pbb.getPlantBaseBlockIdDTO(),
                         Bukkit.getScheduler()
                                 .runTaskLater(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), () -> {
                                     final IPlantConcept rescan = checkIfBlockHasPlantConceptOtherwiseDelete(pbb);

                                     if (rescan == null) {
                                         delayedTasks.remove(pbb.getPlantBaseBlockIdDTO());
                                         return;
                                     }

                                     evaluateAgeOfPlantAndUpdateInDatabaseIfNotMatureDeleteAndContinueQueue(rescan,
                                                                                                            pbb);
                                     delayedTasks.remove(pbb.getPlantBaseBlockIdDTO());
                                 }, 1));
    }

    public void evaluateAgeOfPlantAndUpdateInDatabaseIfNotMatureDeleteAndContinueQueue(IPlantConcept ipc,
                                                                                       PlantBaseBlockDTO pbb) {
        PlantDataUtils.fillPlantBaseBlockDTOWithCurrentAgeAndNextUpdateTimestamp(ipc, pbb);

        if (ifMatureDeleteAndReturnTrue(ipc, pbb)) {
            return;
        }

        PlantBaseBlockDAO.getInstance().updatePlantBaseBlock(pbb);
        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }

    @Override
    public void onUnexpectedRegisteredPlantPlayerBreakEvent(IPlantConcept ipc,
                                                            PlantBaseBlockDTO pbb,
                                                            Block brokenBlock) {

        if (!(ipc instanceof IPlantConceptLocation)) {
            throw new RuntimeException("Plant did not provide location information");
        }

        IPlantConceptLocation loc = (IPlantConceptLocation) ipc;

        if (brokenBlock.equals(loc.getPlantRootBlock(brokenBlock))) {
            boolean wasDeleted = PlantBaseBlockDAO.getInstance().deletePlantBaseBlock(brokenBlock);
            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("{0} at {1} {2} deleted.",
                                              brokenBlock.getType(),
                                              brokenBlock.getLocation().toVector(),
                                              wasDeleted ?
                                                      "was successfully" :
                                                      "has failed to be"));

            return;
        }

        if (!(ipc instanceof IPlantConceptMultiBlockGrowthVertical)) {
            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("{0} at {1} was part of a plant structure. The plugin does not " +
                                                      "support a modification of that structure yet and is ignoring " +
                                                      "it.",
                                              brokenBlock.getType(),
                                              brokenBlock.getLocation().toVector()));
            return;
        }

        onPlantPlayerRescanEventAsync(pbb);
    }

    @Override
    public void onUnexpectedUnregisteredPlantPlayerBreakEvent(IPlantConcept ipc,
                                                              PlantBaseBlockDTO pbb,
                                                              Block brokenBlock) {
        if (!(ipc instanceof IPlantConceptLocation)) {
            throw new RuntimeException("Plant did not provide location information");
        }

        if (!(ipc instanceof IPlantConceptMultiBlockGrowthVertical)) {
            return;
        }

        // onUnexpectedUnregisteredPlantPlayerPlaceEvent already has a delayed task
        onUnexpectedUnregisteredPlantPlayerPlaceEvent(ipc, pbb);
    }

    public IPlantConcept checkIfBlockHasPlantConceptOtherwiseDelete(PlantBaseBlockDTO plant) {
        IPlantConcept ipc = ControlledPlantGrowthManager.getInstance()
                .retrieveSuitedPlantConcept(plant.getLocation().getBlock().getType());

        if (ipc == null) {
            PlantBaseBlockDAO.getInstance().deletePlantBaseBlock(plant.getLocation().getBlock());
            return null;
        }

        return ipc;
    }

    @Override
    public void onDTOPlantGrowthRequest(PlantBaseBlockDTO plant) {
        if (plant == null) {
            return;
        }

        IPlantConcept ipc = checkIfBlockHasPlantConceptOtherwiseDelete(plant);

        if (ipc == null) {
            return;
        }

        // TODO if server was stopped for f. e. 5h ... should the plant be fully grown? maybe introduce setting for that

        IPlantConceptGrowthInformation conceptGrowthInformation = (IPlantConceptGrowthInformation) ipc;

        conceptGrowthInformation.increaseGrowthStep(plant.getLocation().getBlock());

        if (ifMatureDeleteAndReturnTrue(ipc, plant)) {
            return;
        }

        PlantDataUtils.fillPlantBaseBlockDTOWithCurrentAgeAndNextUpdateTimestamp(ipc, plant);
        PlantBaseBlockDAO.getInstance().updatePlantBaseBlock(plant);
    }

    public boolean ifMatureDeleteAndReturnTrue(IPlantConcept ipc,
                                                      PlantBaseBlockDTO plant) {
        IPlantConceptGrowthInformation conceptGrowthInformation = (IPlantConceptGrowthInformation) ipc;

        if (!conceptGrowthInformation.isMature(plant.getLocation().getBlock())) {
            return false;
        }

        // TODO instead of deleting the block maybe updating age level?
        PlantBaseBlockDAO.getInstance().deletePlantBaseBlock(plant.getLocation().getBlock());
        return true;
    }
}
