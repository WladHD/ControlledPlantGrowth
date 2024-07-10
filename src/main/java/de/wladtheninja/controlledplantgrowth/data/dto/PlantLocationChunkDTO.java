package de.wladtheninja.controlledplantgrowth.data.dto;

import com.google.common.base.Objects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PlantLocationChunkDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private UUID worldUID;
    private int x;
    private int z;

    private boolean loaded;

    @Transient
    public String toString() {
        return MessageFormat.format("[X{0}, Z{1}, loaded? {2}]", x, z, loaded);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(worldUID, x, z, loaded);
    }

    @Transient
    public boolean equals(Object o) {
        if (!(o instanceof PlantLocationChunkDTO)) {
            return false;
        }

        final PlantLocationChunkDTO ppb = (PlantLocationChunkDTO) o;

        return x == ppb.getX() && ppb.getZ() == z && ppb.getWorldUID().compareTo(worldUID) == 0 &&
                ppb.isLoaded() == loaded;
    }
}
