package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.listeners.debug.PlayerBlockDebugListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

@RequiredArgsConstructor
public class SetupDebugEvents implements Runnable {

    private final Plugin plugin;

    @Override
    public void run() {
        getServer().getPluginManager().registerEvents(new PlayerBlockDebugListener(), plugin);
    }
}
