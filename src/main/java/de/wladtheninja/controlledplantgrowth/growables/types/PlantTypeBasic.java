package de.wladtheninja.controlledplantgrowth.growables.types;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthConstraints;
import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptLocation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantConstraintViolationException;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PlantTypeBasic
        implements IPlantConceptBasic, IPlantConceptGrowthConstraints, IPlantConceptLocation {
    private final ArrayList<IPlantGrowthConstraint> constraints;
    private final List<Material> acceptedMaterials;

    public PlantTypeBasic(List<Material> acceptedMaterials) {
        this(acceptedMaterials, null);
    }

    public PlantTypeBasic(List<Material> acceptedMaterials,
                          List<IPlantGrowthConstraint> constraints) {

        this.constraints = new ArrayList<>();
        this.acceptedMaterials = new ArrayList<>();

        if (acceptedMaterials != null) {
            this.acceptedMaterials.addAll(acceptedMaterials);
        }

        if (constraints != null) {
            this.constraints.addAll(constraints);
        }
    }

    @Override
    public List<Material> getAcceptedPlantMaterials() {
        return acceptedMaterials;
    }

    @Override
    public void addAcceptedPlantMaterial(Material... mat) {
        if (mat == null) {
            return;
        }

        acceptedMaterials.addAll(Arrays.asList(mat));
    }

    @Override
    public List<IPlantGrowthConstraint> checkGrowthConstraintViolations(Block b) {
        return getGrowthConstraints().stream()
                .filter(iPlantGrowthConstraint -> !iPlantGrowthConstraint.isGrowthConditionFulfilled(this, b))
                .collect(Collectors.toList());
    }

    @Override
    public void addGrowthConstraint(IPlantGrowthConstraint constraint) {
        if (constraint == null) {
            return;
        }

        constraints.add(constraint);
    }

    @Override
    public List<IPlantGrowthConstraint> getGrowthConstraints() {
        return constraints;
    }

    @Override
    public void handleConstraintCheckOrElseThrowError(IPlantConceptBasic ipc,
                                                      Block b)
            throws PlantConstraintViolationException {
        List<IPlantGrowthConstraint> violations = checkGrowthConstraintViolations(b);

        if (violations.isEmpty()) {
            return;
        }

        throw new PlantConstraintViolationException(violations, b, ipc);
    }
}
