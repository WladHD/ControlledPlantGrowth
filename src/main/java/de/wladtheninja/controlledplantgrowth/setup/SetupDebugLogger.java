package de.wladtheninja.controlledplantgrowth.setup;

import org.bukkit.Bukkit;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class SetupDebugLogger implements Runnable {
    @Override
    public void run() {
        Bukkit.getLogger().setLevel(Level.FINER);
        Bukkit.getLogger().setFilter(record -> {
            record.setLevel(Level.FINER);
            return true;
        });

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);

        Bukkit.getLogger().addHandler(consoleHandler);

        Bukkit.getLogger().log(Level.FINER, "Logging level set to FINER");
    }
}
