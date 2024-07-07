package de.wladtheninja.controlledplantgrowth.listeners;

import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.event.world.ChunkUnloadEvent;

import java.text.MessageFormat;
import java.util.logging.Level;

public class BlockInteractionEventListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerLoadEvent(ServerLoadEvent event) {
        Bukkit.getLogger().log(Level.FINER, "Starting to look out for plants to grow.");

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
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (!SettingsDAO.getInstance()
                .getCurrentSettings()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants()) {
            return;
        }

        ControlledPlantGrowthManager.getInstance().getChunkAnalyser().chunkUnloaded(event.getChunk());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

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

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakEvent(BlockBreakEvent event) {
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
        IPlantConcept ipc =
                ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(event.getBlockPlaced().getType());

        if (ipc == null) {
            return;
        }

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onArtificialGrowthHarvestInlineEvent(ipc, event.getBlock(), false);
    }
}
