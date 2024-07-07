package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlockFruitAttached;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;

public class PlantInstancePumpkin extends PlantTypeAgeingOneBlockFruitAttached {


    public PlantInstancePumpkin() {
        // see https://minecraft.fandom.com/wiki/Pumpkin_Seeds for accepted grow blocks
        super(Material.PUMPKIN,
                Material.PUMPKIN_STEM,
                Material.ATTACHED_PUMPKIN_STEM,
                Arrays.asList(Material.DIRT,
                        Material.COARSE_DIRT,
                        Material.ROOTED_DIRT,
                        Material.GRASS_BLOCK,
                        Material.FARMLAND,
                        Material.PODZOL,
                        Material.MYCELIUM,
                        Material.MOSS_BLOCK,
                        Material.MUD,
                        Material.MUDDY_MANGROVE_ROOTS),
                Collections.singletonList(new LightLevelPlantGrowthConstraint((byte) 9)));
    }
}
