package de.wladtheninja.controlledplantgrowth.data.dto.embedded;

import com.google.common.base.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class PlantLocation3dDTO
        implements Serializable
{
    private UUID worldUID;
    private int x;
    private int y;
    private int z;

    @Transient
    public String toString() {
        return MessageFormat.format("[{0}, {1}, {2}]", x, y, z);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(worldUID, x, y, z);
    }

    @Transient
    public boolean equals(Object o) {
        if (!(o instanceof PlantLocation3dDTO)) {
            return false;
        }

        final PlantLocation3dDTO ppb = (PlantLocation3dDTO) o;

        return x == ppb.x && y == ppb.y && ppb.z == z && ppb.worldUID.compareTo(worldUID) == 0;
    }
}
