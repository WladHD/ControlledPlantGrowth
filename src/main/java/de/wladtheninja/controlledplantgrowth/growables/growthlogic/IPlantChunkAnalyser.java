package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import org.bukkit.Chunk;

public interface IPlantChunkAnalyser {

    void onChunkLoaded(Chunk c);

    void onChunkUnloaded(Chunk c);

}
