package de.wladtheninja.controlledplantgrowth.setup;

import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.logging.*;

public class SetupDebugLogger implements Runnable {
    @Override
    public void run() {
        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel(Level.ALL);
        systemOut.setFormatter(new CustomFormatter());
        Bukkit.getLogger().addHandler(systemOut);
        Bukkit.getLogger().setLevel(Level.ALL);

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Logging level set to {0} (DEBUG OPTION ENABLED)", systemOut.getLevel()));
    }

    private static class CustomFormatter extends SimpleFormatter {
        public String format(LogRecord record) {
            return MessageFormat.format("[ControlledPlantGrowth {1}] {0}\n", record.getMessage(), record.getLevel());
        }
    }
}
