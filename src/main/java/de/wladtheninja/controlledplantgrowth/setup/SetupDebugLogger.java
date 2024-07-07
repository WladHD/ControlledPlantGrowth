package de.wladtheninja.controlledplantgrowth.setup;

import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.logging.*;

public class SetupDebugLogger implements Runnable {
    @Override
    public void run() {
        Bukkit.getLogger().setLevel(Level.ALL);
        Bukkit.getLogger().setFilter(record -> {
            record.setLevel(Level.ALL);
            return true;
        });

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new CustomFormatter());

        Bukkit.getLogger().addHandler(consoleHandler);


        Bukkit.getLogger().log(Level.FINER, "Logging level set to FINER");
    }

    private static class CustomFormatter extends SimpleFormatter {
        public String format(LogRecord record) {
            return MessageFormat.format("[ControlledPlantGrowth DEBUG] {0}\n", record.getMessage());
        }
    }
}
