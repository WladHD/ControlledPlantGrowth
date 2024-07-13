package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlantInstanceWheatCo extends PlantTypeAgeingOneBlock {
    public PlantInstanceWheatCo() {
        super(Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.POTATOES, Material.CARROTS,
                        Material.NETHER_WART, Material.SWEET_BERRY_BUSH),
                Collections.singletonList(new LightLevelPlantGrowthConstraint((byte) 9)));
    }
}
