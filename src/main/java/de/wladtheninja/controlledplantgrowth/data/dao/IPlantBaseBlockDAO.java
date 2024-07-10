package de.wladtheninja.controlledplantgrowth.data.dao;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public interface IPlantBaseBlockDAO<T> {

    List<T> getAll();

    List<T> merge(List<T> obj);

    T merge(T obj);

    boolean delete(Location l);

    T getByLocation(Location l);

    List<T> getByMaterial(Material material);

    List<T> getByChunk(Chunk c);

    List<T> getAfterTimestamp(long timeStamp, int groupMilliseconds, int limit);

    List<T> getBeforeTimestamp(long timeStamp);

    void handleException(Exception ex);

}
