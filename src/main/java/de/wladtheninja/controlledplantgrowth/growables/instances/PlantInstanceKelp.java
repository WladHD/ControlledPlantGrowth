package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingMultiBlockVerticalMaxAgeRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;

import java.util.Collections;
import java.util.List;

public class PlantInstanceKelp
        extends PlantTypeAgeingMultiBlockVerticalMaxAgeRandom
{

    private final int settingsMaxAge = ((Ageable) Material.KELP.createBlockData()).getMaximumAge();

    public PlantInstanceKelp() {
        super(Material.KELP, Material.KELP_PLANT);
    }

    @Override
    public Material getDatabasePlantType(Block b) {
        return Material.KELP;
    }

    @Override
    public List<Material> getAcceptedSettingPlantMaterials() {
        return Collections.singletonList(Material.KELP);
    }

    @Override
    public int getCurrentAge(Block b) {
        int age = -1;
        final Block plantRootBlock = getPlantRootBlock(b);

        for (Block current = plantRootBlock;
             containsAcceptedMaterial(current.getType());
             current = current.getRelative(BlockFace.UP)) {
            age++;
        }

        if (age < 0) {
            throw new RuntimeException("PlantRootBlock is missing. Perform sanity check before calling plant methods.");
        }

        return age;
    }

    @Override
    public void setCurrentAge(Block b, int age) {
        final Block plantRootBlock = getPlantRootBlock(b);

        if (isMature(plantRootBlock)) {
            return;
        }

        int currentAge = getCurrentAge(plantRootBlock);

        // replace previous block with KELP_PLANT
        for (int i = Math.max(0, currentAge - 1); i <= age; i++) {
            final Block current = plantRootBlock.getRelative(0, i, 0);

            if (current.getType() == Material.KELP_PLANT) {
                continue;
            }

            if (current.getType() != Material.WATER && current.getType() != Material.KELP) {
                getRandomMaxAgeMap().put(plantRootBlock.getLocation(), age);
                break;
            }

            Material plantMat = Material.KELP_PLANT;

            if (i == age || plantRootBlock.getRelative(0, i + 1, 0).getType() != Material.WATER) {
                plantMat = Material.KELP;
            }

            current.setType(plantMat, true);
            current.setBlockData(plantMat.createBlockData());
            current.getState().update();
        }
    }

    @Override
    public int getSettingsMaximalAge(Material material) {
        return settingsMaxAge;
    }

    @Override
    public int getMaxAgeUpperBound() {
        return settingsMaxAge;
    }

    @Override
    public int getMaxAgeLowerBound() {
        return 3;
    }
}
