package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptMaxAgeRandom;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
public abstract class PlantTypeAgeingMultiBlockVerticalMaxAgeRandom extends PlantTypeAgeingMultiBlockVertical
        implements IPlantConceptMaxAgeRandom {

    private final Material materialSapling;
    private final Material materialStem;
    private final Random random;
    private final HashMap<Location, Integer> randomMaxAgeMap;

    public PlantTypeAgeingMultiBlockVerticalMaxAgeRandom(Material materialSapling,
                                                         Material materialStem,
                                                         List<IPlantGrowthConstraint> constraints) {
        super(Arrays.asList(materialStem, materialSapling), constraints);
        this.materialSapling = materialSapling;
        this.materialStem = materialStem;
        this.random = new Random();
        this.randomMaxAgeMap = new HashMap<>();
    }

    @Override
    public Material getDatabasePlantType(Block b) {
        return getMaterialStem();
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public void removeFromMaxAgeMap(Location location) {
        if (location == null) {
            return;
        }

        getRandomMaxAgeMap().remove(location);
    }


    @Override
    public void setCurrentAge(Block b, int setAge) {
        if (setAge > 0 && b.getType() == getMaterialSapling()) {
            b.setType(getMaterialStem());
        }

        super.setCurrentAge(b, setAge);
    }

    @Override
    public void onPlantRemoved(Location location) {
        removeFromMaxAgeMap(location);
        super.onPlantRemoved(location);
    }

    @Override
    public int getMaximumAge(Block b) {
        if (getRandomMaxAgeMap().containsKey(b.getLocation())) {
            return getMaxAgeFromMap(b.getLocation());
        }

        getRandomMaxAgeMap().put(b.getLocation(),
                getMaxAgeUpperBound() - getRandom().nextInt((getMaxAgeUpperBound() - getMaxAgeLowerBound()) + 1));

        return getMaxAgeFromMap(b.getLocation());
    }

    @Override
    public int getMaxAgeFromMap(Location location) {
        if (location == null) {
            return -1;
        }

        return getRandomMaxAgeMap().getOrDefault(location, -1);
    }
}
