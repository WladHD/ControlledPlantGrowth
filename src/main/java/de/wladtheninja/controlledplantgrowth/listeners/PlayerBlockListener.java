package de.wladtheninja.controlledplantgrowth.listeners;

import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptLocation;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.text.MessageFormat;
import java.util.logging.Level;

public class PlayerBlockListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHarvestBlockEvent(ServerLoadEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     "Server loaded event received. Starting Clockwork.");

        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHarvestBlockEvent(PlayerHarvestBlockEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Player {0} has harvested {1} at {2}",
                                          event.getPlayer().getName(),
                                          event.getHarvestedBlock().getType(),
                                          event.getHarvestedBlock().getLocation().toVector()));

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockGrowEvent(BlockGrowEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Block {1} at {2} wants to grow from {3} to {4}",
                                          event.getBlock().getType(),
                                          event.getBlock().getLocation().toVector(),
                                          ((Ageable) event.getBlock().getState()).getAge(),
                                          ((Ageable) event.getNewState()).getAge()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Player {0} is breaking {1} at {2}",
                                          event.getPlayer().getName(),
                                          event.getBlock().getType(),
                                          event.getBlock().getLocation().toVector()));

        IPlantConcept ipc = ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(event.getBlock().getType());

        if (ipc == null) {
            return;
        }

        Block plantRoot = event.getBlock();

        if (ipc instanceof IPlantConceptLocation) {
            IPlantConceptLocation loc = (IPlantConceptLocation) ipc;
            plantRoot = loc.getPlantRootBlock(plantRoot);
        }

        PlantBaseBlockDTO registeredPlant = PlantBaseBlockDAO.getInstance().getPlantBaseBlockByBlock(plantRoot);

        if (registeredPlant == null) {
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onUnexpectedUnregisteredPlantPlayerBreakEvent(ipc,
                                                                   new PlantBaseBlockDTO(plantRoot.getLocation()),
                                                                   event.getBlock());

        }
        else {
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onUnexpectedRegisteredPlantPlayerBreakEvent(ipc, registeredPlant, event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Player {0} has placed something on {1} at {2}",
                                          event.getPlayer().getName(),
                                          event.getBlockAgainst().getType(),
                                          event.getBlockAgainst().getLocation().toVector()));

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Placed {0} with coords {1}",
                                          event.getBlockPlaced().getType(),
                                          event.getBlockPlaced().getLocation().toVector()));

        IPlantConcept ipc =
                ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(event.getBlockPlaced().getType());

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("The placed block had {0} plant concept",
                                          ipc == null ?
                                                  "no" :
                                                  "a retrieved"));

        if (ipc == null) {
            return;
        }

        Block plantRoot = event.getBlockPlaced();

        if (ipc instanceof IPlantConceptLocation) {
            IPlantConceptLocation loc = (IPlantConceptLocation) ipc;
            plantRoot = loc.getPlantRootBlock(plantRoot);
        }

        PlantBaseBlockDTO registeredPlant = PlantBaseBlockDAO.getInstance().getPlantBaseBlockByBlock(plantRoot);

        if (registeredPlant != null) {
            Bukkit.getLogger()
                    .log(Level.FINER, MessageFormat.format("The newly placed {0} is part of a registered plant " +
                                                                   "structure. Updating.", plantRoot.getType()));

            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onUnexpectedRegisteredPlantPlayerPlaceEvent(ipc, registeredPlant);
        }
        else {
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onUnexpectedUnregisteredPlantPlayerPlaceEvent(ipc, new PlantBaseBlockDTO(plantRoot.getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Player {0} has interacted with {1} at {2}",
                                          event.getPlayer().getName(),
                                          event.getClickedBlock().getType(),
                                          event.getClickedBlock().getLocation().toVector()));

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("The above block is of type {0} at {1}",
                                          event.getClickedBlock().getRelative(0, 1, 0).getType(),
                                          event.getClickedBlock().getRelative(0, 1, 0).getLocation().toVector()));

    }
}
