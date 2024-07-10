package de.wladtheninja.controlledplantgrowth.growables.concepts.err;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.text.MessageFormat;

@Getter
@Setter
public class PlantRootBlockMissingException extends Exception {

    private final Block plantRoot;

    public PlantRootBlockMissingException(Block plantRoot) {
        this(MessageFormat.format("Plant {0} at {1} does not have a root block.",
                plantRoot.getType(),
                plantRoot.getLocation().toVector()), plantRoot);
    }

    public PlantRootBlockMissingException(String err, Block plantRoot) {
        super(err);
        this.plantRoot = plantRoot;
    }
}
