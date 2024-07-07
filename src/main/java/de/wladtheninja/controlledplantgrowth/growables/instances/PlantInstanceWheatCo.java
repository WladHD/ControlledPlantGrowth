package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlock;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;

public class PlantInstanceWheatCo extends PlantTypeAgeingOneBlock {
    public PlantInstanceWheatCo() {
        super(Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.POTATOES),
                Collections.singletonList(new LightLevelPlantGrowthConstraint((byte) 9)));
    }
}
