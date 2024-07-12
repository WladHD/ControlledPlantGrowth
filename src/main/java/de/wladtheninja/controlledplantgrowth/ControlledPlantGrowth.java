package de.wladtheninja.controlledplantgrowth;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.setup.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public final class ControlledPlantGrowth extends JavaPlugin {

    private final Runnable[] setups;

    public ControlledPlantGrowth() {
        setups = new Runnable[]{
                new SetupDatabase(),
                new SetupSettings(),
                new SetupPlantConcepts(),
                new SetupEvents(this),
                new SetupCommands()
        };
    }

    @Override
    public void onEnable() {
        new SetupConfig().run();

        if (PlantDataManager.getInstance().getSettingsDataBase().getCurrentConfig().isEnableDebugLog()) {
            new SetupDebugLogger().run();
        }

        Arrays.stream(setups).forEach(Runnable::run);
    }


    public static void handleException(Exception ex) {
        Bukkit.getLogger().log(Level.INFO, ex.getMessage(), ex);
    }


    @Override
    public void onDisable() {
        DatabaseHibernateUtil.getInstance().shutdown();
    }
}
