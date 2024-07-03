package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.constraints.IPlantGrowthConstraint;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConceptGrowthConstraints extends IPlantConcept {

    boolean isAllowedToGrow(Block b);

    void addGrowthConstraint(IPlantGrowthConstraint constraint);

    List<IPlantGrowthConstraint> getGrowthConstraints();
}
