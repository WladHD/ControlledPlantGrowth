package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConceptTree
        extends IPlantConceptBasic
{

    List<Block> getGiant2x2Structure(Block b);

    Material getSaplingType();

    TreeType getTreeType();

    TreeType getGiantTreeType();

    default boolean isGiantTreeSupported() {
        return getGiantTreeType() != null;
    }
}
