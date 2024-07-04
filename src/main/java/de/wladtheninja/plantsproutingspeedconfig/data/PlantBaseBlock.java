package de.wladtheninja.plantsproutingspeedconfig.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.bukkit.Location;

@Entity
public class PlantBaseBlock {
    private final int x;
    private final int y;
    private final int z;
    private final String world;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public PlantBaseBlock(Location loc) {
        if (loc.getWorld() == null) {
            throw new RuntimeException("World is null...");
        }

        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        world = loc.getWorld().getName();
    }

}