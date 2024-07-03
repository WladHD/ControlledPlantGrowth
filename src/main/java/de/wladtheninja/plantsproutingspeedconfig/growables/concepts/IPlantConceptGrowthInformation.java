package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import org.bukkit.block.Block;

public interface IPlantConceptGrowthInformation extends IPlantConcept {

    boolean isMature(Block b);

    void setToFullyMature(Block b);

}
