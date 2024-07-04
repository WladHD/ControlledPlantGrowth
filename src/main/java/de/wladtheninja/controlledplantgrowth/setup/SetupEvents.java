package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.listeners.debug.PlayerBlockClickEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

@RequiredArgsConstructor
public class SetupEvents implements Runnable {

    private final Plugin plugin;

    @Override
    public void run() {
        getServer().getPluginManager().registerEvents(new PlayerBlockClickEvent(), plugin);
    }
}
