package de.wladtheninja.controlledplantgrowth.data.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Entity
@Getter
@Setter
public class PlantBaseBlockDTO {
    private int x;
    private int y;
    private int z;

    private int xChunk;
    private int zChunk;

    private int currentPlantStage;
    private long timeNextGrowthStage;
    private long timeFullGrowthStage;

    private transient Location location;

    private UUID worldUID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public PlantBaseBlockDTO(Location loc) {
        this();
        if (loc.getWorld() == null) {
            throw new RuntimeException("World is null...");
        }

        setX(loc.getBlockX());
        setY(loc.getBlockY());
        setZ(loc.getBlockZ());

        setWorldUID(loc.getWorld().getUID());
    }

    public PlantBaseBlockDTO() {
        setCurrentPlantStage(-1);
        setTimeFullGrowthStage(-1);
        setTimeFullGrowthStage(-1);
    }

    public Location getLocation() {
        final World world = Bukkit.getWorld(worldUID);

        if (world == null) {
            return null;
        }

        if (location == null) {
            // TODO efficient to keep object persisted during runtime? maybe just return the object idk
            setLocation(new Location(world, x, y, z));
        }

        return location;
    }
}