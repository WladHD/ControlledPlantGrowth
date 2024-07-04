package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.growables.PlantConceptManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceWheatCo;

public class SetupPlantConcepts implements Runnable {
    @Override
    public void run() {
        PlantConceptManager.getInstance().registerPlantConceptInstance(new PlantInstanceWheatCo());
    }
}
