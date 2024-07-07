package de.wladtheninja.controlledplantgrowth.listeners;

import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.text.MessageFormat;
import java.util.logging.Level;

public class BlockInteractionEventListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHarvestBlockEvent(ServerLoadEvent event) {
        Bukkit.getLogger().log(Level.FINER, "Server loaded event received. Starting Clockwork.");

        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!SettingsDAO.getInstance()
                .getCurrentSettings()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants()) {
            return;
        }

        ControlledPlantGrowthManager.getInstance().getChunkAnalyser().checkForPlantsInChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        Bukkit.getLogger().log(Level.FINER, MessageFormat.format("Entity {0} changed {1} at {2} to {3}",
                                          event.getEntity().getName(),
                                          event.getBlock().getType(),
                                          event.getBlock().getLocation().toVector(),
                                          event.getTo()));

        boolean breakEvent = event.getTo() == Material.AIR;


        IPlantConcept ipc = ControlledPlantGrowthManager.getInstance()
                .retrieveSuitedPlantConcept(breakEvent ?
                                                    event.getBlock().getType() :
                                                    event.getTo());

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), breakEvent);
    }


    // WHAT EVEN IS THAT LOL
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
        IPlantConcept ipc =
                ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(event.getBlock().getType());

        if (ipc == null) {
            return;
        }

        if (SettingsDAO.getInstance().getCurrentSettings().isDisableNaturalGrowth()) {
            event.setCancelled(true);
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Block {0} at {1} wants to grow (current age {2})",
                                          event.getBlock().getType(),
                                          event.getBlock().getLocation().toVector(),
                                          (event.getBlock().getBlockData() instanceof Ageable ?
                                                  ((Ageable) event.getBlock().getBlockData()).getAge() :
                                                  "<No Age Info>")));

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Player {0} is breaking {1} at {2}",
                                          event.getPlayer().getName(),
                                          event.getBlock().getType(),
                                          event.getBlock().getLocation().toVector()));

        IPlantConcept ipc =
                ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(event.getBlock().getType());

        if (ipc == null) {
            return;
        }

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), true);

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

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), false);
    }
}
