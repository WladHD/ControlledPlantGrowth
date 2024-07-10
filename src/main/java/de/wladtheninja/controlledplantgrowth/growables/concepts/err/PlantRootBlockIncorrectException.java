package de.wladtheninja.controlledplantgrowth.growables.concepts.err;

import org.bukkit.block.Block;

import java.text.MessageFormat;

public class PlantRootBlockIncorrectException extends PlantRootBlockMissingException {

    public PlantRootBlockIncorrectException(Block plantRoot) {
        super(MessageFormat.format("Plant {0} at {1} provided an incorrect root block.",
                plantRoot.getType(),
                plantRoot.getLocation().toVector()), plantRoot);
    }
}
