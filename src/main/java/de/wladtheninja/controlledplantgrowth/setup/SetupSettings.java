package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;

public class SetupSettings implements Runnable {

    @Override
    public void run() {
        PlantDataManager.getInstance().getSettingsDataBase().loadCurrentSettingsAndCache();
    }
}
