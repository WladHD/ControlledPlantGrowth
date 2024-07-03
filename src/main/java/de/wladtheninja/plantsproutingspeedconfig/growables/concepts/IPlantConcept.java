package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import org.bukkit.Material;

import java.util.List;

public interface IPlantConcept {

    List<Material> getAcceptedPlantMaterials();

    void addAcceptedPlantMaterial(Material... mat);

}
