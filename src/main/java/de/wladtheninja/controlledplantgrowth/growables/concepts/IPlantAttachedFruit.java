package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IPlantAttachedFruit {

    Block getPlantRootBlockByFruitBlock(Block b);

    Material getFruitMaterial();

    Material getStemMaterial();

    Material getAttachedStemMaterial();
}
