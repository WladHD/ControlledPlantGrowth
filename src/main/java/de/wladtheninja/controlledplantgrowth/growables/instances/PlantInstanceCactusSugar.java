package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingMultiBlockUpwards;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;

public class PlantInstanceCactusSugar extends PlantTypeAgeingMultiBlockUpwards {

    public PlantInstanceCactusSugar() {
        super(Arrays.asList(Material.CACTUS, Material.SUGAR_CANE));
    }

    @Override
    public int getMaximumAge(Block b) {
        return 2;
    }
}
