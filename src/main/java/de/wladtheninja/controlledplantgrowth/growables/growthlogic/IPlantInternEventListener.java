package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IPlantInternEventListener {

    // onArtificialGrowthRegisteredPlant
    void onArtificialGrowthRegisteredPlantEvent(IPlantConcept ipc,
                                                PlantBaseBlockDTO pbb);

    void queueRecheckOfBlock(IPlantConcept ipc, Block b);

    default void onArtificialGrowthHarvestInlineEvent(IPlantConcept ipc,
                                                      Block potentialPlantRoot,
                                                      boolean breakEvent) {
        if (ipc == null || potentialPlantRoot == null) {
            return;
        }

        if (breakEvent) {
            onArtificialHarvestEvent(ipc, potentialPlantRoot);
        }
        else {
            onArtificialGrowthEvent(ipc, potentialPlantRoot);
        }
    }

    void onForcePlantsReloadByTypeEvent(Material material);

    void onArtificialGrowthEvent(IPlantConcept ipc,
                                 Block placedBlock);

    void onArtificialGrowthEvent(IPlantConcept ipc,
                                 Block placedBlock,
                                 boolean ifExistsIgnore);

    void onArtificialGrowthUnregisteredPlantEvent(IPlantConcept ipc,
                                                  PlantBaseBlockDTO pbb);

    void onArtificialHarvestRegisteredPlantEvent(IPlantConcept ipc,
                                                 PlantBaseBlockDTO pbb,
                                                 Block brokenBlock);

    void onArtificialHarvestUnregisteredPlantEvent(IPlantConcept ipc,
                                                   PlantBaseBlockDTO pbb,
                                                   Block brokenBlock);

    void onArtificialHarvestEvent(IPlantConcept ipc,
                                  Block brokenBlock);

    void requestGrowthForPlant(PlantBaseBlockDTO pbb);
}
