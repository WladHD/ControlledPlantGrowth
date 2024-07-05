package de.wladtheninja.controlledplantgrowth.growables.concepts;

import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConceptGrowthConstraints extends IPlantConcept {

    List<IPlantGrowthConstraint> checkGrowthConstraintViolations(Block b);

    void addGrowthConstraint(IPlantGrowthConstraint constraint);

    List<IPlantGrowthConstraint> getGrowthConstraints();

    void handleConstraintCheckOrElseThrowError(IPlantConcept ipc,
                                               Block b)
            throws PlantConstraintViolationException;
}