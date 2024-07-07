package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockIdDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class PlantInternEventListener implements IPlantInternEventListener {

    HashMap<PlantBaseBlockIdDTO, BukkitTask> delayedTasks = new HashMap<>();

    @Override

    public void onArtificialGrowthRegisteredPlantEvent(IPlantConcept ipc,
                                                       PlantBaseBlockDTO pbb) {

        onPlantPlayerRescanEventAsync(pbb);
    }

    @Override
    public void queueRecheckOfBlock(IPlantConcept ipc, Block b) {

        Bukkit.getScheduler().runTaskLater(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), () -> {
            evaluateAgeOfPlantAndUpdateInDatabaseIfMatureDeleteAndContinueQueue(ipc,
                    new PlantBaseBlockDTO(b.getLocation(), b.getType()));
        }, 1);
    }

    @Override
    public void onForcePlantsReloadByTypeEvent(Material material) {
        List<PlantBaseBlockDTO> list = PlantBaseBlockDAO.getInstance().getPlantBaseBlocksByType(material);


        list.forEach(s -> {
            IPlantConcept ipc = ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(material);
            evaluateAgeOfPlantAndUpdateInDatabaseIfMatureDeleteAndContinueQueue(ipc, s);
        });

        Bukkit.getLogger()
                .log(Level.INFO,
                     MessageFormat.format("Plant {0} was successfully reloaded. {1} plants were affected.",
                                          material,
                                          list.size()));
    }

    @Override
    public void onArtificialGrowthEvent(IPlantConcept ipc,
                                        Block plantRoot) {
        onArtificialGrowthEvent(ipc, plantRoot, false);
    }

    @Override
    public void onArtificialGrowthEvent(IPlantConcept ipc,
                                        Block plantRoot,
                                        boolean ifExistsIgnore) {
        if (!plantRoot.getChunk().isLoaded()) {
            return;
        }

        if (ipc instanceof IPlantAttachedFruit) {
            IPlantAttachedFruit iPlantAttachedFruit = (IPlantAttachedFruit) ipc;
            if (iPlantAttachedFruit.getFruitMaterial() == plantRoot.getType() &&
                    iPlantAttachedFruit.getPlantRootBlockByFruitBlock(plantRoot) == null) {
                return;
            }
        }

        if (ipc instanceof IPlantConceptLocation) {
            IPlantConceptLocation loc = (IPlantConceptLocation) ipc;
            plantRoot = loc.getPlantRootBlock(plantRoot);
        }

        PlantBaseBlockDTO registeredPlant = PlantBaseBlockDAO.getInstance().getPlantBaseBlockByBlock(plantRoot);

        if (ifExistsIgnore && registeredPlant != null) {
            return;
        }

        if (registeredPlant != null) {
            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("Natural growth occurred for registered structure. Update.",
                                              plantRoot.getType()));

            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onArtificialGrowthRegisteredPlantEvent(ipc, registeredPlant);
        }
        else {
            if (plantRoot.getType() == Material.AIR) {
                return;
            }

            if (ipc instanceof IPlantConceptGrowthInformation &&
                    ((IPlantConceptGrowthInformation) ipc).isMature(plantRoot)) {
                return;
            }

            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onArtificialGrowthUnregisteredPlantEvent(ipc,
                            new PlantBaseBlockDTO(plantRoot.getLocation(),
                                    plantRoot.getType()));
        }
    }

    @Override
    public void onArtificialGrowthUnregisteredPlantEvent(IPlantConcept ignored,
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

                                     evaluateAgeOfPlantAndUpdateInDatabaseIfMatureDeleteAndContinueQueue(rescan, pbb);
                                     delayedTasks.remove(pbb.getPlantBaseBlockIdDTO());
                                 }, 1));
    }

    public void evaluateAgeOfPlantAndUpdateInDatabaseIfMatureDeleteAndContinueQueue(IPlantConcept ipc,
                                                                                    PlantBaseBlockDTO pbb) {
        if (pbb.getLocation().getBlock().getType() == Material.AIR) {
            PlantBaseBlockDAO.getInstance().deletePlantBaseBlock(pbb.getLocation().getBlock());
            return;
        }

        PlantDataUtils.fillPlantBaseBlockDTOWithCurrentAgeAndNextUpdateTimestamp(ipc, pbb);

        if (ifMatureDeleteAndReturnTrue(ipc, pbb)) {
            return;
        }

        PlantBaseBlockDAO.getInstance().updatePlantBaseBlock(pbb);
        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }

    @Override
    public void onArtificialHarvestRegisteredPlantEvent(IPlantConcept ipc,
                                                        PlantBaseBlockDTO pbb,
                                                        Block brokenBlock) {

        if (!(ipc instanceof IPlantConceptLocation)) {
            throw new RuntimeException("Plant did not provide location information");
        }

        IPlantConceptLocation loc = (IPlantConceptLocation) ipc;

        if (ipc instanceof IPlantAttachedFruit) {
            onPlantPlayerRescanEventAsync(pbb);
            return;
        }


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
    public void onArtificialHarvestUnregisteredPlantEvent(IPlantConcept ipc,
                                                          PlantBaseBlockDTO pbb,
                                                          Block brokenBlock) {
        if (!(ipc instanceof IPlantConceptLocation)) {
            throw new RuntimeException("Plant did not provide location information");
        }

        if (!(ipc instanceof IPlantConceptMultiBlockGrowthVertical)) {
            return;
        }

        // onUnexpectedUnregisteredPlantPlayerPlaceEvent already has a delayed task
        onArtificialGrowthUnregisteredPlantEvent(ipc, pbb);
    }

    @Override
    public void onArtificialHarvestEvent(IPlantConcept ipc,
                                         Block plantRoot) {

        if (ipc instanceof IPlantConceptLocation) {
            IPlantConceptLocation loc = (IPlantConceptLocation) ipc;
            plantRoot = loc.getPlantRootBlock(plantRoot);
        }

        PlantBaseBlockDTO registeredPlant = PlantBaseBlockDAO.getInstance().getPlantBaseBlockByBlock(plantRoot);

        if (registeredPlant == null) {
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onArtificialHarvestUnregisteredPlantEvent(ipc,
                                                               new PlantBaseBlockDTO(plantRoot.getLocation(),
                                                                                     plantRoot.getType()),
                                                               plantRoot);

        }
        else {
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onArtificialHarvestRegisteredPlantEvent(ipc, registeredPlant, plantRoot);
        }

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
    public void requestGrowthForPlant(PlantBaseBlockDTO plant) {
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
