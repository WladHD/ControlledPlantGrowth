package de.wladtheninja.controlledplantgrowth.growables.growthlogic.exec;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RequestPlantGrowthRunnable implements Runnable {

    private final List<PlantBaseBlockDTO> plants;
    private final boolean updateQueueWhenCompleted;


    @Override
    public void run() {
        if (plants == null || plants.isEmpty()) {
            return;
        }

        plants.forEach(pl -> ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onPossiblePlantStructureModifyEvent(pl.getPlantType(), pl.getLocation()));

        if (updateQueueWhenCompleted) {
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
        }
    }
}