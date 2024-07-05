package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptLocation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

import java.util.List;

public abstract class PlantTypeAgeingOneBlock extends PlantTypeBasic implements IPlantConceptAge,
        IPlantConceptLocation {

    public PlantTypeAgeingOneBlock(List<Material> acceptedMaterials) {
        super(acceptedMaterials);
    }

    public PlantTypeAgeingOneBlock(List<Material> acceptedMaterials,
                                   List<IPlantGrowthConstraint> constraints) {
        super(acceptedMaterials, constraints);
    }

    @Override
    public int getCurrentAge(Block b) {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new RuntimeException("Block does not inherit Ageable interface.");
        }

        final Ageable ag = (Ageable) b.getBlockData();
        return ag.getAge();
    }

    @Override
    public void setCurrentAge(Block b,
                              int age) {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new RuntimeException("Block does not inherit Ageable interface.");
        }

        try {
            handleConstraintCheckOrElseThrowError(this, b);
        } catch (PlantConstraintViolationException e) {
            e.printInformation();
            return;
        }

        final Ageable ag = (Ageable) b.getBlockData();
        ag.setAge(Math.min(age, getMaximumAge(b)));
        b.setBlockData(ag);
    }

    @Override
    public void increaseGrowthStep(Block b) {
        setCurrentAge(b, getCurrentAge(b) + 1);
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
    public Block getGroundBlock(Block b) {
        // assuming this plant will only grow on the ground
        return b.getRelative(0, -1, 0);
    }

    @Override
    public Block getPlantRootBlock(Block b) {
        return b;
    }

    @Override
    public Material getPlantRootMaterial(Block b) {
        return getPlantRootBlock(b).getType();
    }
}
