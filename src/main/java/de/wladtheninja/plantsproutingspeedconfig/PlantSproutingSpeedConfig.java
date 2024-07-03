package de.wladtheninja.plantsproutingspeedconfig;

import de.wladtheninja.plantsproutingspeedconfig.listeners.debug.PlayerBlockClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public final class PlantSproutingSpeedConfig extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().setLevel(Level.FINER);
        Bukkit.getLogger().setFilter(record -> {
            record.setLevel(Level.FINER);
            return true;
        });

        // TODO REMOVE
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);

        Bukkit.getLogger().addHandler(consoleHandler);

        Bukkit.getLogger().log(Level.FINER, "Logging level set to FINER");


        getServer().getPluginManager().registerEvents(new PlayerBlockClickEvent(), this);
    }

    @Override
    public void onDisable() {

    }
}
