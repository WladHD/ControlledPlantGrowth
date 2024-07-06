package de.wladtheninja.controlledplantgrowth;

import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.setup.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class ControlledPlantGrowth extends JavaPlugin {

    private final Runnable[] setups;

    public ControlledPlantGrowth() {
        setups = new Runnable[]{new SetupDatabase(), new SetupSettings(), new SetupPlantConcepts(),
                new SetupEvents(this)};
    }

    @Override
    public void onEnable() {
        new SetupConfig().run();

        if (SettingsDAO.getInstance().getCurrentConfig().isEnableDebugLog()) {
            new SetupDebugLogger().run();
        }

        Arrays.stream(setups).forEach(Runnable::run);
    }

    @Override
    public void onDisable() {
        DatabaseHibernateUtil.getInstance().shutdown();
    }
}
