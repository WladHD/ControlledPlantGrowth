package de.wladtheninja.plantsproutingspeedconfig.growables.instances;

import de.wladtheninja.plantsproutingspeedconfig.growables.types.PlantTypeAgeingOneBlock;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class PlantInstanceWheat extends PlantTypeAgeingOneBlock {
    @Override
    public int getCurrentAge() {
        return 0;
    }

    @Override
    public void setCurrentAge(int age) {

    }

    @Override
    public int getMaximumAge() {
        return 0;
    }

    @Override
    public boolean isMature() {
        return false;
    }

    @Override
    public void setToFullyMature() {

    }

    @Override
    public Vector getGroundBlockVector() {
        return null;
    }

    @Override
    public Vector getPlantRootVector() {
        return null;
    }

    @Override
    public Material getSproutMaterial() {
        return null;
    }
}
