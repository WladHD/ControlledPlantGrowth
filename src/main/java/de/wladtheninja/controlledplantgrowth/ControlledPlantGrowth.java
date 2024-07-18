package de.wladtheninja.controlledplantgrowth;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateLocalPlantCacheUtil;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateSettingsUtil;
import de.wladtheninja.controlledplantgrowth.setup.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;

public final class ControlledPlantGrowth
        extends JavaPlugin
{

    public final static int SPIGOT_RESOURCE_ID = 117871;
    public final static long GITHUB_REPOSITORY_ID = 824412371;
    private final Runnable[] setups;

    public ControlledPlantGrowth() {
        setups = new Runnable[]{
                new SetupCheckUpdate(),
                new SetupDatabase(),
                new SetupSettings(),
                new SetupPlantInstances(),
                new SetupEvents(this),
                new SetupCommands()
        };
    }

    public static void handleException(Exception ex, Level level) {
        if (ex != null) {
            Bukkit.getLogger().log(level, ex.getMessage(), ex);
        }

        if (level == Level.SEVERE) {
            Bukkit.getLogger()
                  .log(
                          Level.SEVERE,
                          MessageFormat.format(
                                  "Disabling {0} ...",
                                  ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class)
                          )
                  );
            Bukkit.getPluginManager().disablePlugin(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class));
        }
    }

    public static void handleException(Exception ex) {
        handleException(ex, Level.INFO);
    }

    @Override
    public void onEnable() {
        new SetupConfig().run();

        if (PlantDataManager.getInstance().getConfigDataBase().getCurrentConfigFromCache().isEnableDebugLog()) {
            new SetupDebugLogger().run();
        }

        Arrays.stream(setups).forEach(Runnable::run);

        Bukkit.getLogger().info("ControlledPlantGrowth is enabled!");
    }

    @Override
    public void onDisable() {
        DatabaseHibernateLocalPlantCacheUtil.getInstance().shutdown();
        DatabaseHibernateSettingsUtil.getInstance().shutdown();
    }
}
