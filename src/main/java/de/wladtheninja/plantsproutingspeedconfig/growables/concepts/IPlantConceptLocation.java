package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IPlantConceptLocation extends IPlantConcept {

    // CAN BE TREE?
    Block getGroundBlock(Block b);

    Block getPlantRootBlock(Block b);

    Material getSproutMaterial(Block b);

}
