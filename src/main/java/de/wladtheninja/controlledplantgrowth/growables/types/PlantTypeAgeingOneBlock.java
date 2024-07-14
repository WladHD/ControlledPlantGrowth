package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptLocation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantRootBlockMissingException;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

import java.util.Collections;
import java.util.List;

public abstract class PlantTypeAgeingOneBlock extends PlantTypeBasic
        implements IPlantConceptAge, IPlantConceptLocation {

    public PlantTypeAgeingOneBlock(List<Material> acceptedMaterials) {
        super(acceptedMaterials);
    }

    public PlantTypeAgeingOneBlock(List<Material> acceptedMaterials, List<IPlantGrowthConstraint> constraints) {
        super(acceptedMaterials, constraints);
    }

    @Override
    public List<Location> getPlantComplexLocations(Block b) {
        return Collections.singletonList(b.getLocation());
    }

    @Override
    public int getSettingsMaximalAge(Material material) {
        BlockData bd = material.createBlockData();

        if (!(bd instanceof Ageable)) {
            return 1;
        }

        return ((Ageable) bd).getMaximumAge();
    }

    @Override
    public int getCurrentAge(Block b) throws PlantNoAgeableInterfaceException {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new PlantNoAgeableInterfaceException(b);
        }

        final Ageable ag = (Ageable) b.getBlockData();
        return ag.getAge();
    }

    @Override
    public void setCurrentAge(Block b, int age) throws PlantNoAgeableInterfaceException {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new PlantNoAgeableInterfaceException(b);
        }

        try {
            handleConstraintCheckOrElseThrowError(this, b);
        }
        catch (PlantConstraintViolationException e) {
            e.printInformation();
            return;
        }

        final Ageable ag = (Ageable) b.getBlockData();
        ag.setAge(Math.min(age, getMaximumAge(b)));
        b.setBlockData(ag);
    }

    @Override
    public void increaseGrowthStep(Block b) {
        try {
            setCurrentAge(b, getCurrentAge(b) + 1);
        }
        catch (PlantNoAgeableInterfaceException e) {
            ControlledPlantGrowth.handleException(e);
        }
    }

    @Override
    public int getMaximumAge(Block b) throws PlantNoAgeableInterfaceException {
        if (!(b.getBlockData() instanceof Ageable)) {
            throw new PlantNoAgeableInterfaceException(b);
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
    public @NonNull Block getPlantRootBlock(Block b) throws PlantRootBlockMissingException {
        return b;
    }

    @Override
    public Material getPlantRootMaterial(Block b) throws PlantRootBlockMissingException {
        return getPlantRootBlock(b).getType();
    }
}
