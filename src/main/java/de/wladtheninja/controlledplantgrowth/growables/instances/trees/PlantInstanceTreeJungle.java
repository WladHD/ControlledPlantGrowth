package de.wladtheninja.controlledplantgrowth.growables.instances.trees;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeTree;
import org.bukkit.Material;
import org.bukkit.TreeType;

public class PlantInstanceTreeJungle extends PlantTypeTree {
    public PlantInstanceTreeJungle() {
        super(Material.JUNGLE_SAPLING, TreeType.SMALL_JUNGLE, TreeType.JUNGLE);
    }
}
