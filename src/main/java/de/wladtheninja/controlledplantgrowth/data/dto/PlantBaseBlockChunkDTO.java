package de.wladtheninja.controlledplantgrowth.data.dto;

import com.google.common.base.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class PlantBaseBlockChunkDTO implements Serializable {
    private int xChunk;
    private int zChunk;
    private boolean markedAsOfflineChunk;

    @Override
    public int hashCode() {
        return Objects.hashCode(xChunk, zChunk, markedAsOfflineChunk);
    }

    @Transient
    public boolean equals(Object o) {
        if (!(o instanceof PlantBaseBlockChunkDTO)) {
            return false;
        }

        final PlantBaseBlockChunkDTO ppb = (PlantBaseBlockChunkDTO) o;

        return xChunk == ppb.xChunk && zChunk == ppb.zChunk && markedAsOfflineChunk == ppb.markedAsOfflineChunk;
    }
}
