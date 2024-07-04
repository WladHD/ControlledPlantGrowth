package de.wladtheninja.controlledplantgrowth.data.dto;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Entity
@Getter
@Setter
public class PlantBaseBlockDTO {

    @EmbeddedId
    private PlantBaseBlockIdDTO plantBaseBlockIdDTO;

    private int xChunk;
    private int zChunk;
    private boolean markedAsOfflineChunk;

    private int currentPlantStage;
    private long timeNextGrowthStage;

    private transient Location location;

    public PlantBaseBlockDTO(Location loc) {
        this();
        if (loc.getWorld() == null) {
            throw new RuntimeException("World is null...");
        }

        plantBaseBlockIdDTO.setX(loc.getBlockX());
        plantBaseBlockIdDTO.setY(loc.getBlockY());
        plantBaseBlockIdDTO.setZ(loc.getBlockZ());
        plantBaseBlockIdDTO.setWorldUID(loc.getWorld().getUID());

        setXChunk(loc.getChunk().getX());
        setZChunk(loc.getChunk().getZ());

    }

    public PlantBaseBlockDTO() {
        plantBaseBlockIdDTO = new PlantBaseBlockIdDTO();
        setCurrentPlantStage(-1);
        setTimeNextGrowthStage(-1);
    }

    @Transient
    public Location getLocation() {
        final World world = Bukkit.getWorld(plantBaseBlockIdDTO.getWorldUID());

        if (world == null) {
            return null;
        }

        if (location == null) {
            // TODO efficient to keep object persisted during runtime? maybe just return the object idk
            setLocation(new Location(world,
                                     plantBaseBlockIdDTO.getX(),
                                     plantBaseBlockIdDTO.getY(),
                                     plantBaseBlockIdDTO.getZ()));
        }

        return location;
    }

    @Transient
    public boolean equals(Object o) {
        if (!(o instanceof PlantBaseBlockDTO)) {
            return false;
        }

        return ((PlantBaseBlockDTO) o).plantBaseBlockIdDTO.equals(plantBaseBlockIdDTO);
    }
}