package de.wladtheninja.controlledplantgrowth.growables.instances.trees;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeTree;
import org.bukkit.Material;
import org.bukkit.TreeType;

public class PlantInstanceTreeSpruce
        extends PlantTypeTree
{
    public PlantInstanceTreeSpruce() {
        super(Material.SPRUCE_SAPLING, TreeType.REDWOOD, TreeType.MEGA_REDWOOD);
    }
}
