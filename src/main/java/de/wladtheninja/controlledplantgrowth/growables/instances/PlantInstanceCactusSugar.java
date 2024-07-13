package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingMultiBlockVertical;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;

public class PlantInstanceCactusSugar extends PlantTypeAgeingMultiBlockVertical {

    public PlantInstanceCactusSugar() {
        super(Arrays.asList(Material.CACTUS, Material.SUGAR_CANE));
    }

    @Override
    public int getMaximumAge(Block b) {
        return 2;
    }

    @Override
    public int getSettingsMaximalAge(Material material) {
        return 2;
    }

}
