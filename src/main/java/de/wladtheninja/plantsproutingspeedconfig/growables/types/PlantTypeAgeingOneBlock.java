package de.wladtheninja.plantsproutingspeedconfig.growables.types;

import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConceptAge;
import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConceptGrowthConstraints;
import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConceptGrowthInformation;
import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConceptLocation;
import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.constraints.IPlantGrowthConstraint;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class PlantTypeAgeingOneBlock implements IPlantConceptGrowthInformation, IPlantConceptAge,
        IPlantConceptLocation, IPlantConceptGrowthConstraints {

    ArrayList<IPlantGrowthConstraint> constraints;

    public PlantTypeAgeingOneBlock() {
        constraints = new ArrayList<>();
    }

    public PlantTypeAgeingOneBlock(List<IPlantGrowthConstraint> constraints) {
        this();

        if (constraints == null) {
            return;
        }

        getGrowthConstraints().addAll(constraints);
    }

    @Override
    public boolean isAllowedToGrow(Block b) {
        for (IPlantGrowthConstraint pgc : getGrowthConstraints()) {
            if (!pgc.isGrowthConditionFulfilled(this, b)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void addGrowthConstraint(IPlantGrowthConstraint constraint) {
        if (constraint == null) {
            return;
        }

        getGrowthConstraints().add(constraint);
    }

    @Override
    public List<IPlantGrowthConstraint> getGrowthConstraints() {
        return constraints;
    }

    @Override
    public int getCurrentAge(Block b) {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new RuntimeException("Block dies not inherit Ageable interface.");
        }

        final Ageable ag = (Ageable) b.getBlockData();
        return ag.getAge();
    }

    @Override
    public void setCurrentAge(Block b,
                              int age) {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new RuntimeException("Block dies not inherit Ageable interface.");
        }

        if (!isAllowedToGrow(b)) {
            Bukkit.getLogger().log(Level.FINER, "Constraint was not fulfilled, plant can't grow.");
            return;
        }

        final Ageable ag = (Ageable) b.getBlockData();
        ag.setAge(Math.min(age, getMaximumAge(b)));
        b.setBlockData(ag);
    }

    @Override
    public int getMaximumAge(Block b) {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new RuntimeException("Block is registered as Ageable, yet does not inherit Ageable interface.");
        }

        final Ageable ag = (Ageable) b.getBlockData();
        return ag.getMaximumAge();
    }

    @Override
    public boolean isMature(Block b) {
        return getCurrentAge(b) == getMaximumAge(b);
    }

    @Override
    public void setToFullyMature(Block b) {
        setCurrentAge(b, getMaximumAge(b));
    }

    @Override
    public Block getGroundBlock(Block b) {
        return b;
    }

    @Override
    public Block getPlantRootBlock(Block b) {
        // assuming this plant will only grow on the ground
        return b.getRelative(0, 1, 0);
    }

    @Override
    public Material getSproutMaterial(Block b) {
        return getPlantRootBlock(b).getType();
    }
}
