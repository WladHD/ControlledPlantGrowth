package de.wladtheninja.controlledplantgrowth.growables.concepts.basic;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import org.bukkit.block.Block;

public interface IPlantConceptGrowthInformation {

    boolean isMature(Block b);

    void setToFullyMature(Block b);

    void increaseGrowthStep(Block b);

}
