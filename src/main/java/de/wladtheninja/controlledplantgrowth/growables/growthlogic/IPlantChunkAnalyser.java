package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

public interface IPlantChunkAnalyser {

    void onChunkLoaded(Chunk c);

    void onChunkUnloaded(Chunk c);

    void clearChunkCache();

    void notifyCommandSenderOnQueueFinish(CommandSender sender);

}
