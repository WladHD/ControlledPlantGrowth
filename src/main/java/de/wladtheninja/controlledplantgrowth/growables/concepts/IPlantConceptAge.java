package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.block.Block;

public interface IPlantConceptAge extends IPlantConceptGrowthInformation {

    int getCurrentAge(Block b);

    void setCurrentAge(Block b,
                       int age);

    int getMaximumAge(Block b);

    @Override
    default boolean isMature(Block b) {
        return getCurrentAge(b) >= getMaximumAge(b);
    }

    @Override
    default void setToFullyMature(Block b) {
        setCurrentAge(b, getMaximumAge(b));
    }
}
