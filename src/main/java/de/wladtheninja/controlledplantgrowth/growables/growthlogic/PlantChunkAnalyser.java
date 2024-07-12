package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlantChunkAnalyser implements IPlantChunkAnalyser {

    @Getter
    private final ArrayDeque<ChunkSnapshot> arrayDeque;
    private final ScheduledExecutorService scheduledExecutorService;

    private final List<CommandSender> notifyOnDepletion;

    @Getter
    private final List<ChunkCordsStore> analyzedChunks;

    @Getter
    private List<BlockData> cacheSupportedBlockData;
    @Getter
    private List<Material> cacheSupportedMaterials;

    private boolean running;

    @AllArgsConstructor
    @Getter
    public static class ChunkCordsStore {
        public ChunkCordsStore(Chunk c) {
            this(c.getX(), c.getZ(), c.getWorld().getUID());
        }

        private final int x;
        private final int z;
        private final UUID world;

        public boolean isEqualTo(Chunk c) {
            return x == c.getX() && z == c.getZ() && world.equals(c.getWorld().getUID());
        }

        public String toString() {
            return MessageFormat.format("{0},{1}", x, z);
        }
    }

    public PlantChunkAnalyser() {
        analyzedChunks = new ArrayList<>();
        arrayDeque = new ArrayDeque<>();
        notifyOnDepletion = new ArrayList<>();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onChunkLoaded(Chunk cs) {
        if (cs == null) {
            return;
        }

        analyzedChunks.add(new ChunkCordsStore(cs));

        arrayDeque.add(cs.getChunkSnapshot(true, false, false));

        if (!running) {
            asyncSearch();
        }
    }

    @Override
    public void onChunkUnloaded(Chunk c) {
        boolean removedPreAnalyze = arrayDeque.removeIf(chunkSnapshot -> chunkSnapshot.getX() == c.getX() &&
                chunkSnapshot.getZ() == c.getZ() && c.getWorld().getName().equals(chunkSnapshot.getWorldName()));

        if (!removedPreAnalyze) {
            return;
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Chunk unloaded before analyze was possible: {0}",
                                new ChunkCordsStore(c)));

        analyzedChunks.removeIf(chunkCordsStore -> chunkCordsStore.isEqualTo(c));
    }

    @Override
    public void clearChunkCache() {
        analyzedChunks.clear();
    }

    @Override
    public void notifyCommandSenderOnQueueFinish(CommandSender sender) {
        if (notifyOnDepletion.contains(sender)) {
            return;
        }

        notifyOnDepletion.add(sender);
    }

    public void reloadSupportedMaterials() {
        cacheSupportedBlockData = new ArrayList<>();
        cacheSupportedMaterials = new ArrayList<>();
        ControlledPlantGrowthManager.getInstance().retrieveAllSupportedMaterials().forEach(sm -> {
            cacheSupportedMaterials.add(sm);
            cacheSupportedBlockData.add(sm.createBlockData());
        });
    }

    public void handleNotify() {
        if (notifyOnDepletion.isEmpty()) {
            return;
        }

        notifyOnDepletion.forEach(sender -> {
            try {
                sender.sendMessage("All loaded chunks have been analyzed.");
            }
            catch (Exception ignored) {
                // player disconnected or sth
            }
        });

        notifyOnDepletion.clear();
    }

    public void asyncSearch() {
        if (running) {
            return;
        }

        if (arrayDeque.isEmpty()) {
            handleNotify();
            return;
        }

        if (cacheSupportedBlockData == null) {
            reloadSupportedMaterials();
        }

        running = true;

        scheduledExecutorService.execute(() -> {
            try {
                final long begin = System.currentTimeMillis();

                if (arrayDeque.isEmpty()) {
                    handleNotify();
                    return;
                }

                final ChunkSnapshot cs = arrayDeque.pop();

                List<BlockData> foundMats = cacheSupportedBlockData.stream()
                        .filter(cs::contains)
                        .collect(Collectors.toList());

                World world = Bukkit.getWorld(cs.getWorldName());

                if (world == null) {
                    throw new RuntimeException();
                }

                int minHeight = world.getMinHeight();
                int maxHeight = world.getMaxHeight();

                int xChunk = cs.getX();
                int zChunk = cs.getZ();

                // may god bless my soul
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {

                        for (int y = minHeight; y < maxHeight; y++) {

                            IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance()
                                    .retrieveSuitedPlantConcept(cs.getBlockType(x, y, z));

                            if (ipc == null) {
                                continue;
                            }

                            ControlledPlantGrowthManager.getInstance()
                                    .getInternEventListener()
                                    .onPossiblePlantStructureModifyEvent(cs.getBlockType(x, y, z),
                                            new Location(world, x + xChunk * 16, y, z + zChunk * 16));
                        }
                    }
                }

                final long finish = System.currentTimeMillis();

                if (foundMats.isEmpty()) {
                    return;
                }

                Bukkit.getLogger()
                        .log(Level.FINER,
                                MessageFormat.format("In Chunk {0};{1} found materials: {2} in {3} ms and",
                                        cs.getX(),
                                        cs.getZ(),
                                        Arrays.toString(foundMats.stream().map(BlockData::getMaterial).toArray()),
                                        finish - begin));


            }
            catch (Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
            finally {
                running = false;
                asyncSearch();
            }
        });
    }
}
