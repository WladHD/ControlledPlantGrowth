package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;

public interface IPlantLocationChunkDAO<T, S> {

    List<T> getAll();

    List<PlantBaseBlockDTO> getAllPlantBasesByChunk(World w, int x, int z);

    List<T> merge(List<T> obj);

    T merge(T obj);

    boolean delete(World w, int x, int z);

    T getByChunk(Chunk l);

    T getByChunkCoordinates(World w, int x, int z);

    void handleException(Exception ex);

}
