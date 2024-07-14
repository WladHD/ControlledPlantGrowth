package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingMultiBlockVerticalMaxAgeRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bamboo;

public class PlantInstanceBamboo extends PlantTypeAgeingMultiBlockVerticalMaxAgeRandom {

    public PlantInstanceBamboo() {
        super(Material.BAMBOO_SAPLING, Material.BAMBOO);

        addGrowthConstraint(new LightLevelPlantGrowthConstraint((byte) 9));
    }

    @Override
    public void setCurrentAge(final Block plantRootBlock, int age) throws PlantNoAgeableInterfaceException {
        double maxAge = getMaximumAge(plantRootBlock);

        super.setCurrentAge(plantRootBlock, age);

        int currentAge = getCurrentAge(plantRootBlock);

        for (int i = 0; i <= currentAge; i++) {
            Block current = plantRootBlock.getRelative(0, i, 0);

            double percentRunning = Math.min(i / maxAge, 1);

            if (!containsAcceptedMaterial(current.getType()) && current.getType() != Material.AIR) {
                getRandomMaxAgeMap().put(plantRootBlock.getLocation(), age);
                break;
            }

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
                    (int) Math.round(bamboo.getMaximumAge() * (1 - percentRunning))), bamboo.getMaximumAge()));
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
