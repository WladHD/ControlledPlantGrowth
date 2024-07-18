package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;

public class SetupConfig
        implements Runnable
{

    @Override
    public void run() {
        PlantDataManager.getInstance().getConfigDataBase().loadCurrentConfigAndCache();
    }


}
