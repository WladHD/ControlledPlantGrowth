package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantAttachedFruit;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlantTypeAgeingOneBlockFruitAttached extends PlantTypeAgeingOneBlock implements IPlantAttachedFruit {

    private final Material materialFruit;
    private final Material materialStem;

    private final Material materialAttachedStem;

    @Getter
    private final List<Material> acceptedSoilBlocksForFruit;


    public PlantTypeAgeingOneBlockFruitAttached(Material materialFruit,
                                                Material materialStem,
                                                Material materialAttachedStem,
                                                List<Material> acceptedSoilBlocksForFruit,
                                                List<IPlantGrowthConstraint> constraints) {
        super(Arrays.asList(materialFruit, materialStem, materialAttachedStem), constraints);

        this.acceptedSoilBlocksForFruit = new ArrayList<>();

        this.materialFruit = materialFruit;
        this.materialStem = materialStem;
        this.materialAttachedStem = materialAttachedStem;

        if (acceptedSoilBlocksForFruit != null) {
            this.acceptedSoilBlocksForFruit.addAll(acceptedSoilBlocksForFruit);
        }
    }

    @Override
    public List<Material> getAcceptedSettingPlantMaterials() {
        return Collections.singletonList(materialStem);
    }

    @Override
    public int getCurrentAge(Block b) {
        if (isBlockException(b)) {
            return getMaxPlantAge();
        }

        return super.getCurrentAge(b);
    }

    public boolean isBlockException(Block b) {
        return b.getType() == materialAttachedStem || b.getType() == materialFruit;
    }

    public int getMaxPlantAge() {
        return ((Ageable) materialStem.createBlockData()).getMaximumAge();
    }

    @Override
    public int getMaximumAge(Block b) {
        return getMaxPlantAge() + 1;
    }

    @Override
    public void setCurrentAge(Block b, int age) {
        Bukkit.getLogger().log(Level.FINER, MessageFormat.format("Growing {0}, reaching age: {1}", b.getType(), age));
        if (isBlockException(b)) {
            return;
        }

        try {
            handleConstraintCheckOrElseThrowError(this, b);
        } catch (PlantConstraintViolationException e) {
            e.printInformation();
            return;
        }

        if (age <= getMaxPlantAge()) {
            super.setCurrentAge(b, age);
            return;
        }

        Optional<Block> growthSpot = getNearestBlocks(true).stream()
                .map(b::getRelative)
                .filter(bl -> bl.getType().isAir() &&
                        acceptedSoilBlocksForFruit.contains(bl.getRelative(BlockFace.DOWN).getType()))
                .findFirst();

        if (!growthSpot.isPresent()) {
            Bukkit.getLogger()
                    .log(Level.FINER,
                            MessageFormat.format("No growth spots for {0} at {1} present",
                                    b.getType(),
                                    b.getLocation().toVector()));
            return;
        }

        growthSpot.get().setType(materialFruit);

        b.setType(materialAttachedStem);
        Directional bd = (Directional) b.getBlockData();

        BlockFace bf = b.getFace(growthSpot.get());

        if (bf == null) {
            throw new RuntimeException("Plant stem could not find its fruit");
        }

        bd.setFacing(bf);
        b.setBlockData(bd);
    }

    public List<BlockFace> getNearestBlocks(boolean shuffle) {
        List<BlockFace> nearest = Arrays.asList(BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH);

        if (shuffle) {
            Collections.shuffle(nearest);
        }

        return nearest;
    }

    @Override
    public Block getPlantRootBlockByFruitBlock(Block b) {
        if (b.getType() != materialFruit) {
            throw new RuntimeException(MessageFormat.format("This method is only designed to check the fruit {0}.",
                    materialFruit));
        }

        List<Block> check = getNearestBlocks(false).stream()
                .map(b::getRelative)
                .filter(bl -> bl.getType() == materialAttachedStem)
                .collect(Collectors.toList());

        if (check.isEmpty()) {
            Bukkit.getLogger()
                    .log(Level.FINER,
                            MessageFormat.format("A completely lonely fruit {0} was found at {1}",
                                    b.getType(),
                                    b.getLocation().toVector()));
            return null;
        }

        Optional<Block> foundFruit = check.stream().filter(possibleStem -> {
            if (!(possibleStem.getBlockData() instanceof Directional)) {
                return false;
            }

            Directional possibleStemDir = (Directional) possibleStem.getBlockData();

            return possibleStem.getRelative(possibleStemDir.getFacing()).equals(b);
        }).findFirst();

        return foundFruit.orElse(null);
    }

    @Override
    public Material getFruitMaterial() {
        return materialFruit;
    }

    @Override
    public Material getStemMaterial() {
        return materialStem;
    }

    @Override
    public Material getAttachedStemMaterial() {
        return materialAttachedStem;
    }

    @Override
    public Block getPlantRootBlock(Block b) {
        // redundant, but explicit
        if (b.getType() == materialAttachedStem || b.getType() == materialStem) {
            return b;
        }

        if (b.getType() != materialFruit) {
            throw new RuntimeException("Handed block is neither stem, attached stem or fruit.");
        }

        Block attachedPlantRoot = getPlantRootBlockByFruitBlock(b);

        if (attachedPlantRoot == null) {
            Bukkit.getLogger()
                    .log(Level.FINER,
                            MessageFormat.format("A lonely fruit {0} was found at {1}",
                                    b.getType(),
                                    b.getLocation().toVector()));
            throw new RuntimeException(MessageFormat.format("A lonely fruit {0} was found at {1}",
                    b.getType(),
                    b.getLocation().toVector()));
        }

        return attachedPlantRoot;
    }
}
