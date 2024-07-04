package de.wladtheninja.controlledplantgrowth.growables;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;


public class PlantConceptManager {

    @Getter(lazy = true)
    private static final PlantConceptManager instance = new PlantConceptManager();

    private final List<IPlantConcept> plantConceptInstances;

    private PlantConceptManager() {
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

}
