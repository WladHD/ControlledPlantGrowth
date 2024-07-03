package de.wladtheninja.plantsproutingspeedconfig.growables.concepts.constraints;

import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConcept;
import org.bukkit.block.Block;

public interface IPlantGrowthConstraint {

    // TODO inverse direction to IPlantConcept pos probably not a good design? remove?
    boolean isGrowthConditionFulfilled(IPlantConcept pos,
                                       Block b);

}
