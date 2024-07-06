package de.wladtheninja.controlledplantgrowth.growables.growthlogic.exec;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestPlantGrowthRunnable implements Runnable {

    private static RequestPlantGrowthRunnable instance;
    private List<PlantBaseBlockDTO> plants;
    private boolean updateQueueWhenCompleted;

    public static RequestPlantGrowthRunnable reuseInstanceWith(List<PlantBaseBlockDTO> plants,
                                                               boolean updateQueueWhenCompleted) {

        if (instance == null) {
            instance = new RequestPlantGrowthRunnable();
        }

        instance.plants = plants;
        instance.updateQueueWhenCompleted = updateQueueWhenCompleted;

        return instance;
    }

    @Override
    public void run() {
        if (plants == null || plants.isEmpty()) {
            return;
        }

        plants.forEach(ControlledPlantGrowthManager.getInstance().getInternEventListener()::requestGrowthForPlant);

        if (updateQueueWhenCompleted) {
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
        }
    }
}