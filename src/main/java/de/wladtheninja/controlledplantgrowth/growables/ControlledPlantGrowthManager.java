package de.wladtheninja.controlledplantgrowth.growables;

import de.wladtheninja.controlledplantgrowth.growables.growthlogic.IPlantClockwork;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.IPlantInternEventListener;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.PlantClockwork;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.PlantInternEventListener;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;


public class ControlledPlantGrowthManager {

    @Getter(lazy = true)
    private static final ControlledPlantGrowthManager instance = new ControlledPlantGrowthManager();

    @Getter
    private final IPlantClockwork clockwork = new PlantClockwork();

    @Getter
    private final IPlantInternEventListener internEventListener = new PlantInternEventListener();

    private final List<IPlantConcept> plantConceptInstances;

    private ControlledPlantGrowthManager() {
        plantConceptInstances = new ArrayList<>();
    }

    public void registerPlantConceptInstance(IPlantConcept i) {
        if (i == null) {
            return;
        }

        plantConceptInstances.add(i);
    }

    public IPlantConcept retrieveSuitedPlantConcept(Material m) {
        return plantConceptInstances.stream().filter(pc -> pc.hasAcceptedMaterial(m)).findFirst().orElse(null);
    }

    public boolean hasSuitedPlantConcept(Material m) {
        return plantConceptInstances.stream().anyMatch(pc -> pc.hasAcceptedMaterial(m));
    }

}
