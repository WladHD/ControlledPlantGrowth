package de.wladtheninja.controlledplantgrowth.growables.concepts;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConceptGrowthConstraints extends IPlantConcept {

    boolean isAllowedToGrow(Block b);

    void addGrowthConstraint(IPlantGrowthConstraint constraint);

    List<IPlantGrowthConstraint> getGrowthConstraints();
}
