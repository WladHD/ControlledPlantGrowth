package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.block.Block;

public interface IPlantConceptAge extends IPlantConceptGrowthInformation {

    int getCurrentAge(Block b);

    void setCurrentAge(Block b,
                       int age);

    int getMaximumAge(Block b);

}
