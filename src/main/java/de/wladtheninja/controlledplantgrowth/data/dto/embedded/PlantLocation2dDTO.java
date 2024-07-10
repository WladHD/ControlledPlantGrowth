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
public class PlantLocation2dDTO implements Serializable {
    private UUID worldUID;
    private int x;
    private int z;

    @Transient
    public String toString() {
        return MessageFormat.format("[{0}, {1}]", x, z);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(worldUID, x, z);
    }

    @Transient
    public boolean equals(Object o) {
        if (!(o instanceof PlantLocation2dDTO)) {
            return false;
        }

        final PlantLocation2dDTO ppb = (PlantLocation2dDTO) o;

        return x == ppb.getX() && ppb.getZ() == z && ppb.getWorldUID().compareTo(worldUID) == 0;
    }
}
