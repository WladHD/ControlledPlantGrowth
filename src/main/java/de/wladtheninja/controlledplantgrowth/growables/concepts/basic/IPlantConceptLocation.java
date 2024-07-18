package de.wladtheninja.controlledplantgrowth.growables.concepts.basic;

import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantRootBlockMissingException;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IPlantConceptLocation {

    // CAN BE TREE?
    Block getGroundBlock(Block b);

    @NonNull
    Block getPlantRootBlock(Block b)
            throws PlantRootBlockMissingException;

    Material getPlantRootMaterial(Block b)
            throws PlantRootBlockMissingException;

}
