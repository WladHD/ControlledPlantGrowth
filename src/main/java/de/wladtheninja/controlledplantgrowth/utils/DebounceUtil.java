package de.wladtheninja.controlledplantgrowth.utils;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

// adjusted https://stackoverflow.com/a/38296055
public class DebounceUtil {
    private final ConcurrentHashMap<Object, BukkitTask> delayedMap = new ConcurrentHashMap<>();

    public void debounceUsingBukkitScheduler(final Object key, final Runnable runnable, long delay) {
        final BukkitTask prev = delayedMap.put(key,
                Bukkit.getScheduler()
                        .runTaskLaterAsynchronously(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                                () -> {
                                    try {
                                        Bukkit.getScheduler()
                                                .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                                                        runnable);
                                    }
                                    catch (Exception ex) {
                                        Bukkit.getLogger().log(Level.FINER, ex.getMessage(), ex);
                                    }
                                    finally {
                                        delayedMap.remove(key);
                                    }
                                },
                                delay));

        if (prev != null) {
            prev.cancel();
        }
    }
}