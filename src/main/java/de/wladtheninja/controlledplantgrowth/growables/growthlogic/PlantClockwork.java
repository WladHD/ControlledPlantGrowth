package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.exec.RequestPlantGrowthRunnable;
import de.wladtheninja.controlledplantgrowth.utils.DebounceUtil;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlantClockwork implements IPlantClockwork {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> scheduledFuture;

    @Getter
    private final DebounceUtil debounceUtil = new DebounceUtil();

    public List<PlantBaseBlockDTO> filterLoadedChunks(List<PlantBaseBlockDTO> plants) {
        if (plants == null) {
            return null;
        }

        plants = plants.stream()
                .filter(p -> Objects.requireNonNull(p.getLocation().getWorld())
                        .isChunkLoaded(p.getChunkX(), p.getChunkZ()))
                .collect(Collectors.toList());

        if (plants.isEmpty()) {
            return null;
        }

        return plants;
    }

    public boolean checkOverduePlantUpdates(long currentTime) {
        List<PlantBaseBlockDTO> plants = PlantDataManager.getInstance()
                .getPlantDataBase()
                .getBeforeTimestamp(currentTime);

        plants = filterLoadedChunks(plants);

        if (plants == null) {
            return false;
        }

        requestPlantGrowth(plants, false);
        return true;
    }

    public boolean checkFuturePlantUpdates(long currentTime) {
        List<PlantBaseBlockDTO> plants = PlantDataManager.getInstance()
                .getPlantDataBase()
                .getAfterTimestamp(currentTime,
                        PlantDataManager.getInstance().getSettingsDataBase()
                                .getCurrentSettings()
                                .getMaximumTimeWindowInMillisecondsForPlantsToBeClustered(),
                        PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings().getMaximumAmountOfPlantsInATimeWindowCluster());


        final List<PlantBaseBlockDTO> finalPlants = filterLoadedChunks(plants);

        if (finalPlants == null) {
            return false;
        }

        scheduledFuture = scheduledExecutorService.schedule(() -> requestPlantGrowth(finalPlants, true),
                Math.max(plants.getFirst().getTimeNextGrowthStage() - System.currentTimeMillis(), 0),
                TimeUnit.MILLISECONDS);
        return true;
    }

    public void queueNextBlockToUpdateForceMainThread() {
        if (Bukkit.isPrimaryThread()) {
            startPlantUpdateQueue();
            return;
        }

        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), this::startPlantUpdateQueue);
    }

    @Override
    public void startPlantUpdateQueue() {
        this.startPlantUpdateQueueDebounced();
        //ebounceUtil.debounce(Objects.hash(0), this::startPlantUpdateQueueDebounced, 150, TimeUnit.MILLISECONDS);
    }

    public void startPlantUpdateQueueDebounced() {
        Bukkit.getLogger().log(Level.FINER, "UPDATE QUEUE!");
        if (scheduledFuture != null) {
            Bukkit.getLogger().log(Level.FINER, "CANCELLED!");
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }

        long currentTime = System.currentTimeMillis();

        boolean updatedPast = checkOverduePlantUpdates(currentTime);
        boolean updatedFuture = checkFuturePlantUpdates(currentTime);

        // 1. updatedPast updatedFuture -> return:
        // startPlantUpdateQueue will execute in scheduledExecutorService
        // 2. !updatedPast updatedFuture -> return:
        // same as 1.
        // 3. updatedPast !updatedFuture -> execute startPlantUpdateQueue:
        // past blocks were updated and probably received new timers for updates in the future.
        // startPlantUpdateQueue will not be called otherwise
        // 4. !updatedPast !updatedFuture -> return:
        // no blocks changed, so no evaluation of next plant growth is necessary

        if (!updatedPast || updatedFuture) { // is equivalent to !(updatedPast && !updatedFuture)
            return;
        }

        Bukkit.getScheduler()
                .runTaskLater(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                        this::queueNextBlockToUpdateForceMainThread,
                        2);
    }

    public void requestPlantGrowth(List<PlantBaseBlockDTO> plants, boolean updateQueueWhenCompleted) {
        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                        RequestPlantGrowthRunnable.reuseInstanceWith(plants, updateQueueWhenCompleted));
    }


}
