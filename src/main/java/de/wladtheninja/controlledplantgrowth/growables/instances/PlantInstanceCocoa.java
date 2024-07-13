package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;

import java.util.Collections;

public class PlantInstanceCocoa extends PlantTypeAgeingOneBlock {
    public PlantInstanceCocoa() {
        super(Collections.singletonList(Material.COCOA));
    }

    @Override
    public Block getGroundBlock(Block b) {
        if (!(b.getBlockData() instanceof Directional)) {
            return null;
        }

        return b.getRelative(((Directional) b.getBlockData()).getFacing());
    }

}
