package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceCactusSugar;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceMelon;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstancePumpkin;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceWheatCo;

public class SetupPlantConcepts implements Runnable {
    @Override
    public void run() {
        ControlledPlantGrowthManager.getInstance()
                .registerPlantConceptInstance(new PlantInstanceWheatCo(),
                        new PlantInstanceCactusSugar(),
                        new PlantInstancePumpkin(),
                        new PlantInstanceMelon());
    }
}
