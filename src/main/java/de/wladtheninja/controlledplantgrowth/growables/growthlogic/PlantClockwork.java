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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlantClockwork implements IPlantClockwork {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> scheduledFuture;

    @Getter
    private final DebounceUtil debounceUtil = new DebounceUtil();

    public boolean checkOverduePlantUpdates(long currentTime) {
        List<PlantBaseBlockDTO> oldPlant = PlantDataManager.getInstance()
                .getPlantDataBase()
                .getBeforeTimestamp(currentTime);

        if (oldPlant == null || oldPlant.isEmpty()) {
            return false;
        }

        requestPlantGrowth(oldPlant, false);
        return true;
    }

    public boolean checkFuturePlantUpdates(long currentTime) {
        List<PlantBaseBlockDTO> plants = PlantDataManager.getInstance()
                .getPlantDataBase()
                .getAfterTimestamp(currentTime,
                        SettingsDAO.getInstance()
                                .getCurrentSettings()
                                .getMaximumTimeWindowInMillisecondsForPlantsToBeClustered(),
                        SettingsDAO.getInstance().getCurrentSettings().getMaximumAmountOfPlantsInATimeWindowCluster());

        if (plants == null || plants.isEmpty()) {
            return false;
        }

        scheduledFuture = scheduledExecutorService.schedule(() -> requestPlantGrowth(plants, true),
                Math.max(plants.getFirst().getTimeNextGrowthStage() - System.currentTimeMillis(), 0),
                TimeUnit.MILLISECONDS);
        return true;
    }

    public void queueNextBlockToUpdateForceMainThread() {
        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), this::startPlantUpdateQueue);
    }

    @Override
    public void startPlantUpdateQueue() {
        this.startPlantUpdateQueueDebounced();
        //ebounceUtil.debounce(Objects.hash(0), this::startPlantUpdateQueueDebounced, 150, TimeUnit.MILLISECONDS);
    }

    public void startPlantUpdateQueueDebounced() {
        if (scheduledFuture != null) {
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

        queueNextBlockToUpdateForceMainThread();
    }

    public void requestPlantGrowth(List<PlantBaseBlockDTO> plants,
                                   boolean updateQueueWhenCompleted) {
        Bukkit.getScheduler()
                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                        RequestPlantGrowthRunnable.reuseInstanceWith(plants, updateQueueWhenCompleted));
    }


}
