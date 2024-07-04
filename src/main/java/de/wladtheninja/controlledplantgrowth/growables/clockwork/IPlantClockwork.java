package de.wladtheninja.controlledplantgrowth.growables.clockwork;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;

public interface IPlantClockwork {

    void queueNextBlockToUpdate();

    void onPreSaveNewPlantBaseBlockEvent(IPlantConcept ipc,
                                         PlantBaseBlockDTO pbb);

    void onBreakPlant(IPlantConcept ipc,
                      PlantBaseBlockDTO pbb);

    void onAfterSaveNewPlantEvent();
}
