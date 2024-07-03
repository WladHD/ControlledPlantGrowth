package de.wladtheninja.plantsproutingspeedconfig.growables.concepts;

import org.bukkit.Material;
import org.bukkit.util.Vector;

public interface IPlantConceptSingleMaterial {

    // CAN BE TREE?
    Vector getGroundBlockVector();

    Vector getPlantRootVector();

    Material getSproutMaterial();

}
