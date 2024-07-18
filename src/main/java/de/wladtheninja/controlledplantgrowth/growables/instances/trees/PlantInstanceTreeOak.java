package de.wladtheninja.controlledplantgrowth.growables.instances.trees;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeTree;
import org.bukkit.Material;
import org.bukkit.TreeType;

public class PlantInstanceTreeOak
        extends PlantTypeTree
{
    public PlantInstanceTreeOak() {
        super(Material.OAK_SAPLING, TreeType.TREE, null);
    }
}
