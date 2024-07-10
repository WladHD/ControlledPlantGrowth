package de.wladtheninja.controlledplantgrowth.utils;

import java.util.concurrent.*;

// source from https://stackoverflow.com/a/38296055
public class DebounceUtil {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Object, Future<?>> delayedMap = new ConcurrentHashMap<>();

    /**
     * Debounces {@code callable} by {@code delay}, i.e., schedules it to be executed after {@code delay},
     * or cancels its execution if the method is called with the same key within the {@code delay} again.
     */
    public void debounce(final Object key, final Runnable runnable, long delay, TimeUnit unit) {
        runnable.run();
        /*
        final Future<?> prev = delayedMap.put(key, scheduler.schedule(() -> {
            try {
                Bukkit.getScheduler().runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class), runnable);
            }
            catch (Exception ex) {
                Bukkit.getLogger().log(Level.FINER, ex.getMessage(), ex);
            }
            finally {
                delayedMap.remove(key);
            }
        }, delay, unit));

        if (prev != null) {
            prev.cancel(true);
        }*/
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}