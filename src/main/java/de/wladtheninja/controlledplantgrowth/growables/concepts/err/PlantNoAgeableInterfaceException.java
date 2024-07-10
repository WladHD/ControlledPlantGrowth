package de.wladtheninja.controlledplantgrowth.growables.concepts.err;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.text.MessageFormat;

@Getter
@Setter
public class PlantNoAgeableInterfaceException extends Exception {

    private final Block plantRoot;

    public PlantNoAgeableInterfaceException(Block plantRoot) {
        super(MessageFormat.format("Plant {0} at {1} does not inherit the Ageable interface.",
                plantRoot.getType(),
                plantRoot.getLocation().toVector()));
        this.plantRoot = plantRoot;
    }
}
