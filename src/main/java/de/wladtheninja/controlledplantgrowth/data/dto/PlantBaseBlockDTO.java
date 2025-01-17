package de.wladtheninja.controlledplantgrowth.data.dto;

import de.wladtheninja.controlledplantgrowth.data.dto.embedded.PlantLocation3dDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@Entity(name = "PlantBaseBlockDTO")
@Getter
@Setter
public class PlantBaseBlockDTO {

    @EmbeddedId
    private PlantLocation3dDTO plantBaseBlockIdDTO;

    private int chunkX;
    private int chunkZ;

    private int currentPlantStage;
    private long timeNextGrowthStage;
    private Material plantType;


    private transient Location location;

    public PlantBaseBlockDTO(IPlantConceptBasic ipc, Block b) {
        this();

        plantBaseBlockIdDTO.setX(b.getLocation().getBlockX());
        plantBaseBlockIdDTO.setY(b.getLocation().getBlockY());
        plantBaseBlockIdDTO.setZ(b.getLocation().getBlockZ());
        plantBaseBlockIdDTO.setWorldUID(b.getWorld().getUID());

        setPlantType(ipc.getDatabasePlantType(b));

        // TODO remove ... seems like it was not needed
        setChunkX(b.getChunk().getX());
        setChunkZ(b.getChunk().getZ());
    }

    public PlantBaseBlockDTO() {
        plantBaseBlockIdDTO = new PlantLocation3dDTO();
        setCurrentPlantStage(-1);
        setTimeNextGrowthStage(-1);
        setPlantType(Material.AIR);
    }

    @Transient
    public Location getLocation() {
        final World world = Bukkit.getWorld(plantBaseBlockIdDTO.getWorldUID());

        if (world == null) {
            return null;
        }

        if (location == null) {
            // TODO efficient to keep object persisted during runtime? maybe just return the object idk
            setLocation(new Location(
                    world,
                    plantBaseBlockIdDTO.getX(),
                    plantBaseBlockIdDTO.getY(),
                    plantBaseBlockIdDTO.getZ()
            ));
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