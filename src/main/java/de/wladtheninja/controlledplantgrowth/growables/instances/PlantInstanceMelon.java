package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlockFruitAttached;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;

public class PlantInstanceMelon
        extends PlantTypeAgeingOneBlockFruitAttached
{

    public PlantInstanceMelon() {
        // see https://minecraft.fandom.com/wiki/Melon_Seeds for accepted grow blocks
        super(
                Material.MELON,
                Material.MELON_STEM,
                Material.ATTACHED_MELON_STEM,
                Arrays.asList(
                        Material.DIRT,
                        Material.COARSE_DIRT,
                        Material.ROOTED_DIRT,
                        Material.GRASS_BLOCK,
                        Material.FARMLAND,
                        Material.PODZOL,
                        Material.MYCELIUM,
                        Material.MOSS_BLOCK,
                        Material.MUD,
                        Material.MUDDY_MANGROVE_ROOTS
                ),
                Collections.singletonList(new LightLevelPlantGrowthConstraint((byte) 10))
        );
    }
}
