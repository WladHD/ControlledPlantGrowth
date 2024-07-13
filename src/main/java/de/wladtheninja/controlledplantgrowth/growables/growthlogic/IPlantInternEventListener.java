package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

public interface IPlantInternEventListener {

    void onPlantStructureUpdateEvent(IPlantConceptBasic ipc, Location location);

    void onPossiblePlantStructureModifyEvent(Material possiblePlantMaterial, Location possibleRoot);

    void onForcePlantsReloadByDatabaseTypeEvent(Material mat);
}
