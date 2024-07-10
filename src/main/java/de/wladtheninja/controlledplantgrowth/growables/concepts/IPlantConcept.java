package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConcept {

    List<Material> getAcceptedPlantMaterials();

    default List<Material> getAcceptedSettingPlantMaterials() {
        return getAcceptedPlantMaterials();
    }

    void addAcceptedPlantMaterial(Material... mat);

    default boolean hasAcceptedMaterial(Material mat) {
        return getAcceptedPlantMaterials().stream().anyMatch(mat::equals);
    }

    default Material getDatabasePlantType(Block b) {
        return b.getType();
    }

}
