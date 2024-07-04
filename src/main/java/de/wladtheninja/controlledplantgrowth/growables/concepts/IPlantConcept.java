package de.wladtheninja.controlledplantgrowth.growables.concepts;

import org.bukkit.Material;

import java.util.List;

public interface IPlantConcept {

    List<Material> getAcceptedPlantMaterials();

    void addAcceptedPlantMaterial(Material... mat);

    default boolean hasAcceptedMaterial(Material mat) {
        return getAcceptedPlantMaterials().stream().anyMatch(mat::equals);
    }

}
