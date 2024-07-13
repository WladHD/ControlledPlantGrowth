package de.wladtheninja.controlledplantgrowth.utils;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.*;
import java.util.logging.Level;

// adjusted https://stackoverflow.com/a/38296055
public class DebounceUtil {
    private final ConcurrentHashMap<Object, BukkitTask> delayedMapSpigot = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Object, Future<?>> delayedMap = new ConcurrentHashMap<>();

    /**
     * Debounces {@code callable} by {@code delay}, i.e., schedules it to be executed after {@code delay},
     * or cancels its execution if the method is called with the same key within the {@code delay} again.
     */
    public void debounce(final Object key, final Runnable runnable, long delay, TimeUnit unit) {
        final Future<?> prev = delayedMap.put(key, scheduler.schedule(() -> {
            try {
                runnable.run();
            }
            finally {
                delayedMap.remove(key);
            }
        }, delay, unit));
        if (prev != null) {
            prev.cancel(true);
        }
    }

    public void debounceUsingBukkitScheduler(final Object key, final Runnable runnable, long delay) {
        final BukkitTask prev = delayedMapSpigot.put(key,
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
                                        delayedMapSpigot.remove(key);
                                    }
                                },
                                delay));

        if (prev != null) {
            prev.cancel();
        }
    }
}