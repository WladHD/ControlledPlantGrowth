package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.growables.PlantConceptManager;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceWheatCo;

public class SetupDatabase implements Runnable {
    @Override
    public void run() {
        DatabaseHibernateUtil.setup();
    }
}
