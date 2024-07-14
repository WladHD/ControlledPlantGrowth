package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptTree;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.LightLevelPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class PlantTypeTree extends PlantTypeBasic implements IPlantConceptTree {

    private final Material saplingMaterial;
    private final TreeType saplingTreeType;
    private final TreeType saplingTreeTypeGiant;
    private final BlockFace[] giantCheckFirst = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH
    };

    public PlantTypeTree(Material sapling, TreeType saplingTreeType, TreeType saplingTreeTypeGiant) {
        super(Collections.singletonList(sapling));
        this.saplingMaterial = sapling;
        this.saplingTreeType = saplingTreeType;
        this.saplingTreeTypeGiant = saplingTreeTypeGiant;

        addGrowthConstraint(new LightLevelPlantGrowthConstraint((byte) 9));
    }

    @Override
    public List<Block> getGiant2x2Structure(Block b) {
        List<Block> list = new ArrayList<>();

        for (int i = 0; i < giantCheckFirst.length; i++) {
            boolean deg90 = i % 2 == 0;

            Bukkit.getLogger()
                    .finer(MessageFormat.format("Checking BlockFace {0} {1} a{2} b{3}",
                            giantCheckFirst[i],
                            i,
                            b.getType(),
                            b.getRelative(giantCheckFirst[i]).getType()));

            if (b.getRelative(giantCheckFirst[i]).getType() != b.getType()) {
                list.clear();

                i += deg90 ?
                        1 :
                        0;
                continue;
            }

            list.add(b.getRelative(giantCheckFirst[i]));

            if (list.size() == 3) {
                list.add(b);
                return list;
            }
        }

        return null;
    }

    @Override
    public Material getSaplingType() {
        return saplingMaterial;
    }

    @Override
    public TreeType getTreeType() {
        return saplingTreeType;
    }

    @Override
    public TreeType getGiantTreeType() {
        return saplingTreeTypeGiant;
    }

    @Override
    public int getSettingsMaximalAge(Material material) {
        return 1;
    }

    @Override
    public List<Location> getPlantComplexLocations(Block b) {
        return Collections.singletonList(b.getLocation());
    }

    @Override
    public boolean isMature(Block b) {
        return b.getType() != getSaplingType();
    }

    @Override
    public void setToFullyMature(Block b) {
        try {
            handleConstraintCheckOrElseThrowError(this, b);
        }
        catch (PlantConstraintViolationException e) {
            e.printInformation();
            return;
        }

        int generatedTree;

        List<Block> g2x2 = getGiant2x2Structure(b);

        Bukkit.getLogger()
                .finer(MessageFormat.format("Found structure for {2}: {0} sup? {1}",
                        g2x2 == null ?
                                null :
                                g2x2.size(),
                        isGiantTreeSupported(),
                        getSaplingType()));

        if (g2x2 != null && isGiantTreeSupported()) {
            HashMap<Location, BlockData> cache = new HashMap<>();

            g2x2.forEach(block2x2 -> {
                cache.put(block2x2.getLocation(), block2x2.getBlockData());
                block2x2.setType(Material.AIR);
                block2x2.getState().update();

                if (block2x2 != b) {
                    PlantDataManager.getInstance().getPlantDataBase().delete(block2x2.getLocation());
                }
            });

            generatedTree = b.getWorld().generateTree(b.getLocation(), getGiantTreeType()) ?
                    1 :
                    2;

            if (generatedTree == 2) {
                cache.keySet().forEach(keySetLoc -> {
                    keySetLoc.getBlock().setType(cache.get(keySetLoc).getMaterial());
                    keySetLoc.getBlock().setBlockData(cache.get(keySetLoc));
                    keySetLoc.getBlock().getState().update();
                });
            }
        }
        else {
            final BlockData bd = b.getBlockData();
            b.setType(Material.AIR);
            generatedTree = b.getWorld().generateTree(b.getLocation(), getTreeType()) ?
                    4 :
                    5;
            if (generatedTree != 4) {
                b.setType(getSaplingType());
                b.setBlockData(bd);
                b.getState().update();
            }
        }

        Bukkit.getLogger().finer(MessageFormat.format("Tree Generation resulted in code: {0}", generatedTree));
    }

    @Override
    public void increaseGrowthStep(Block b) {
        setToFullyMature(b);
    }

    @Override
    public Block getGroundBlock(Block b) {
        return b.getRelative(BlockFace.DOWN);
    }

    @Override
    public @NonNull Block getPlantRootBlock(Block b) {
        return b;
    }

    @Override
    public Material getPlantRootMaterial(Block b) {
        return b.getType();
    }
}
