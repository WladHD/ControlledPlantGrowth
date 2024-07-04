package de.wladtheninja.controlledplantgrowth.growables.instances;

import de.wladtheninja.controlledplantgrowth.growables.types.PlantTypeAgeingOneBlock;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlantInstanceWheatCo extends PlantTypeAgeingOneBlock {
    private final List<Material> acceptedMaterials;

    public PlantInstanceWheatCo() {
        acceptedMaterials = new ArrayList<>();

        addAcceptedPlantMaterial(Material.WHEAT, Material.BEETROOT);
    }


    @Override
    public List<Material> getAcceptedPlantMaterials() {
        return acceptedMaterials;
    }

    @Override
    public void addAcceptedPlantMaterial(Material... mat) {
        if (mat == null) {
            return;
        }

        acceptedMaterials.addAll(Arrays.asList(mat));
    }
}
