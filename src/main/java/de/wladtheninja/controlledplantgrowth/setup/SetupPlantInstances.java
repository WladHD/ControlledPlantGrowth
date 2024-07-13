package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.*;
import de.wladtheninja.controlledplantgrowth.growables.instances.trees.*;

public class SetupPlantInstances implements Runnable {
    @Override
    public void run() {
        ControlledPlantGrowthManager.getInstance()
                .registerPlantConceptInstance(new PlantInstanceWheatCo(),
                        new PlantInstanceCactusSugar(),
                        new PlantInstancePumpkin(),
                        new PlantInstanceMelon(),
                        new PlantInstanceBamboo(),
                        new PlantInstanceCocoa(),
                        new PlantInstanceKelp(),
                        new PlantInstanceTreeOak(),
                        new PlantInstanceTreeBirch(),
                        new PlantInstanceTreeSpruce(),
                        new PlantInstanceTreeAcacia(),
                        new PlantInstanceTreeDarkOak(),
                        new PlantInstanceTreeJungle());
    }
}
