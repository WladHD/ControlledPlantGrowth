package de.wladtheninja.controlledplantgrowth.growables.concepts;

import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptGrowthInformation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.basic.IPlantConceptLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public interface IPlantConceptBasic
        extends IPlantConceptLocation, IPlantConceptGrowthInformation
{

    List<Material> getAcceptedPlantMaterials();

    default List<Material> getAcceptedSettingPlantMaterials() {
        return getAcceptedPlantMaterials();
    }

    void addAcceptedPlantMaterial(Material... mat);

    default boolean containsAcceptedMaterial(Material mat) {
        return getAcceptedPlantMaterials().stream().anyMatch(mat::equals);
    }

    default Material getDatabasePlantType(Block b) {
        return b.getType();
    }

    int getSettingsMaximalAge(Material material);

    default void onPlantRemoved(Location removed) {
    }

    List<Location> getPlantComplexLocations(Block b);

}
