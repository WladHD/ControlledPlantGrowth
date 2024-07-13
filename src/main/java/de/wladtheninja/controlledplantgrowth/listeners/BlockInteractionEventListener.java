package de.wladtheninja.controlledplantgrowth.listeners;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
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

import java.util.logging.Level;

public class BlockInteractionEventListener implements Listener {


    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerLoadEvent(ServerLoadEvent event) {
        Bukkit.getLogger().log(Level.FINER, "Starting to look out for plants to grow.");

        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants()) {
            ControlledPlantGrowthManager.getInstance().getChunkAnalyser().onChunkLoaded(event.getChunk());
        }

        ControlledPlantGrowthManager.getInstance().getInternEventListener().onChunkLoadEvent(event.getChunk());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants()) {
            ControlledPlantGrowthManager.getInstance().getChunkAnalyser().onChunkUnloaded(event.getChunk());
        }

        ControlledPlantGrowthManager.getInstance().getInternEventListener().onChunkUnloadEvent(event.getChunk());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(event.getTo() == Material.AIR ?
                        event.getBlock().getType() :
                        event.getTo(), event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerHarvestBlockEvent(PlayerHarvestBlockEvent event) {
        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(event.getHarvestedBlock().getType(),
                        event.getHarvestedBlock().getLocation());
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockGrowEvent(BlockGrowEvent event) {
        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance()
                .retrieveSuitedPlantConcept(event.getBlock().getType());

        // redundant kinda ... nvm
        if (ipc == null) {
            return;
        }

        if (PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .isDisableNaturalGrowth()) {
            event.setCancelled(true);
        }

        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(event.getBlock().getType(), event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(event.getBlock().getType(), event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(event.getBlock().getType(), event.getBlock().getLocation());
    }
}
