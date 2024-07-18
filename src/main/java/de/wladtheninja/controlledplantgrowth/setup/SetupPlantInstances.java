package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.*;
import de.wladtheninja.controlledplantgrowth.growables.instances.trees.*;
import org.bukkit.Material;

public class SetupPlantInstances
        implements Runnable
{
    @Override
    public void run() {
        ControlledPlantGrowthManager.getInstance()
                                    .registerPlantConceptInstance(
                                            new PlantInstanceWheatCo(),
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
                                            new PlantInstanceTreeDarkOak(), new PlantInstanceTreeJungle()
                                    );

        if (Material.getMaterial("CHERRY_SAPLING") != null) {
            ControlledPlantGrowthManager.getInstance().registerPlantConceptInstance(new PlantInstanceTreeCherry());
        }
    }
}
