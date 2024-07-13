package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingMultiBlockVerticalMaxAgeRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bamboo;

import java.util.Collections;

public class PlantInstanceBamboo extends PlantTypeAgeingMultiBlockVerticalMaxAgeRandom {

    public PlantInstanceBamboo() {
        super(Material.BAMBOO_SAPLING,
                Material.BAMBOO,
                Collections.singletonList(new LightLevelPlantGrowthConstraint((byte) 9)));
    }

    @Override
    public void setCurrentAge(Block b, int age) {
        double maxAge = getMaximumAge(b);

        super.setCurrentAge(b, age);

        int currentAge = getCurrentAge(b);

        for (int i = 0; i <= currentAge; i++) {
            Block current = b.getRelative(0, i, 0);

            double percentRunning = Math.min(i / maxAge, 1);
            if (!(current.getBlockData() instanceof Bamboo)) {
                continue;
            }

            Bamboo bamboo = (Bamboo) current.getBlockData();

            bamboo.setLeaves(percentRunning > 0.8 ?
                    Bamboo.Leaves.LARGE :
                    (percentRunning > 0.5 ?
                            Bamboo.Leaves.SMALL :
                            Bamboo.Leaves.NONE));
            current.setBlockData(bamboo);
            bamboo.setAge(Math.min(Math.max(bamboo.getAge(),
                            (int) Math.round(bamboo.getMaximumAge() * (1 - percentRunning))),
                    bamboo.getMaximumAge()));
        }
    }


    @Override
    public int getSettingsMaximalAge(Material material) {
        return 15;
    }

    @Override
    public int getMaxAgeUpperBound() {
        return 15;
    }

    @Override
    public int getMaxAgeLowerBound() {
        return 11;
    }
}
