package de.wladtheninja.controlledplantgrowth.growables.instances.trees;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeTree;
import org.bukkit.Material;
import org.bukkit.TreeType;

public class PlantInstanceTreeCherry
        extends PlantTypeTree
{
    public PlantInstanceTreeCherry() {
        super(Material.CHERRY_SAPLING, TreeType.CHERRY, null);
    }
}
