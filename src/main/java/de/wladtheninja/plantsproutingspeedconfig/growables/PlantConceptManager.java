package de.wladtheninja.plantsproutingspeedconfig.growables;

import de.wladtheninja.plantsproutingspeedconfig.growables.concepts.IPlantConcept;

import java.util.ArrayList;
import java.util.List;

public class PlantConceptManager {

    private static PlantConceptManager _instance = null;

    private List<IPlantConcept> plantConceptInstances;

    private PlantConceptManager() {
        plantConceptInstances = new ArrayList<>();
    }

    public void registerPlantConceptInstance(IPlantConcept i) {

    }

    public static PlantConceptManager getInstance() {
        if (_instance == null) {
            _instance = new PlantConceptManager();
        }

        return _instance;
    }

}
