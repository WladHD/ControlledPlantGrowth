package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import org.bukkit.block.Block;

public interface IPlantConceptAge extends IPlantConcept{

    int getCurrentAge(Block b);

    void setCurrentAge(Block b, int age);

    int getMaximumAge(Block b);

}
