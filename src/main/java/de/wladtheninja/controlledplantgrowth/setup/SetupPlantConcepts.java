package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.*;

public class SetupPlantConcepts implements Runnable {
    @Override
    public void run() {
        ControlledPlantGrowthManager.getInstance()
                .registerPlantConceptInstance(new PlantInstanceWheatCo(),
                        new PlantInstanceCactusSugar(),
                        new PlantInstancePumpkin(),
                        new PlantInstanceMelon(),
                        new PlantInstanceBamboo(),
                        new PlantInstanceCocoa());
    }
}
