package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptMultiBlockGrowthVertical;
import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptLocation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public abstract class PlantTypeAgeingMultiBlockVertical extends PlantTypeBasic
        implements IPlantConceptAge, IPlantConceptLocation, IPlantConceptMultiBlockGrowthVertical {


    public PlantTypeAgeingMultiBlockVertical(List<Material> acceptedMaterials) {
        super(acceptedMaterials);
    }

    @Override
    public List<Location> getPlantComplexLocations(Block b) {
        List<Location> locs = new ArrayList<>();

        for (Block current = b;
             containsAcceptedMaterial(current.getType());
             current = current.getRelative(BlockFace.UP)) {
            locs.add(b.getLocation());
        }

        return locs;
    }

    @Override
    public int getCurrentAge(Block b) {
        int age = -1;
        final Block plantRootBlock = getPlantRootBlock(b);
        final Material plantMaterial = plantRootBlock.getType();

        for (Block current = plantRootBlock;
             current.getType() == plantMaterial;
             current = current.getRelative(BlockFace.UP)) {
            age++;
        }

        if (age < 0) {
            throw new RuntimeException("PlantRootBlock is missing. Perform sanity check before calling plant methods.");
        }

        return age;
    }

    @Override
    public void setCurrentAge(Block b, int setAge) {

        try {
            handleConstraintCheckOrElseThrowError(this, b);
        }
        catch (PlantConstraintViolationException e) {
            e.printInformation();
            return;
        }

        final Block plantRootBlock = getPlantRootBlock(b);

        if (isMature(plantRootBlock)) {
            return;
        }


        int currentAge = getCurrentAge(plantRootBlock);

        for (int i = currentAge; i <= setAge; i++) {
            final Block current = plantRootBlock.getRelative(0, i, 0);

            if (current.getType() == plantRootBlock.getType()) {
                continue;
            }

            if (current.getType() != Material.AIR) {
                break;
            }

            current.setType(plantRootBlock.getType(), true);
            current.getState().update();
        }
    }

    @Override
    public void increaseGrowthStep(Block b) {
        setCurrentAge(b, getCurrentAge(b) + 1);
    }


    @Override
    public Block getGroundBlock(Block b) {
        // assuming this plant will only grow on the ground
        return getPlantRootBlock(b).getRelative(0, -1, 0);
    }

    @Override
    public @NonNull Block getPlantRootBlock(Block b) {

        while (containsAcceptedMaterial(b.getRelative(BlockFace.DOWN).getType())) {
            b = b.getRelative(BlockFace.DOWN);
        }

        return b;
    }

    @Override
    public Material getPlantRootMaterial(Block b) {
        return getPlantRootBlock(b).getType();
    }
}
