package de.wladtheninja.controlledplantgrowth.growables.concepts;

import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptGrowthInformation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.logging.Level;

public interface IPlantConceptAge
        extends IPlantConceptGrowthInformation
{

    int getCurrentAge(Block b)
            throws PlantNoAgeableInterfaceException;

    void setCurrentAge(Block b, int age)
            throws PlantNoAgeableInterfaceException;

    int getMaximumAge(Block b)
            throws PlantNoAgeableInterfaceException;

    @Override
    default boolean isMature(Block b) {
        try {
            return getCurrentAge(b) >= getMaximumAge(b);
        }
        catch (PlantNoAgeableInterfaceException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
            return true;
        }
    }

    @Override
    default void setToFullyMature(Block b) {
        try {
            setCurrentAge(b, getMaximumAge(b));
        }
        catch (PlantNoAgeableInterfaceException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
        }
    }
}
