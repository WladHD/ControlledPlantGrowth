package de.wladtheninja.controlledplantgrowth.growables.concepts.constraints;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import org.bukkit.block.Block;

public interface IPlantGrowthConstraint {

    // TODO inverse direction to IPlantConcept pos probably not a good design? remove?
    boolean isGrowthConditionFulfilled(IPlantConcept pos,
                                       Block b);

}
