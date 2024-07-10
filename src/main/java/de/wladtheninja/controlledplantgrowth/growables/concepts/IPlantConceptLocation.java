package de.wladtheninja.controlledplantgrowth.growables.concepts;

import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantRootBlockMissingException;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IPlantConceptLocation extends IPlantConcept {

    // CAN BE TREE?
    Block getGroundBlock(Block b);

    Block getPlantRootBlock(Block b) throws PlantRootBlockMissingException;

    Material getPlantRootMaterial(Block b) throws PlantRootBlockMissingException;

}
