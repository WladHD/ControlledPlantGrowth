package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import org.bukkit.block.Block;

public interface IPlantInternEventListener {

    void onUnexpectedRegisteredPlantPlayerPlaceEvent(IPlantConcept ipc,
                                                     PlantBaseBlockDTO pbb);

    void onUnexpectedUnregisteredPlantPlayerPlaceEvent(IPlantConcept ipc,
                                                       PlantBaseBlockDTO pbb);

    void onUnexpectedRegisteredPlantPlayerBreakEvent(IPlantConcept ipc,
                                                     PlantBaseBlockDTO pbb,
                                                     Block brokenBlock);

    void onUnexpectedUnregisteredPlantPlayerBreakEvent(IPlantConcept ipc,
                                                       PlantBaseBlockDTO pbb,
                                                       Block brokenBlock);

    void onDTOPlantGrowthRequest(PlantBaseBlockDTO pbb);
}
