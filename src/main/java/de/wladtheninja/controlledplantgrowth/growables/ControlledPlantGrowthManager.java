package de.wladtheninja.controlledplantgrowth.growables;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControlledPlantGrowthManager {

    @Getter(lazy = true)
    private static final ControlledPlantGrowthManager instance = new ControlledPlantGrowthManager();

    @Getter
    private final IPlantClockwork clockwork = new PlantClockwork();

    @Getter
    private final IPlantInternEventListener internEventListener = new PlantInternEventListener();

    @Getter
    private final IPlantChunkAnalyser chunkAnalyser = new PlantChunkAnalyser();

    @Getter
    private final HashMap<Material, IPlantConcept> hashMapRetrieve = new HashMap<>();

    @Getter
    private final List<Material> materialsForSettings = new ArrayList<>();

    public void registerPlantConceptInstance(IPlantConcept... i) {
        Arrays.stream(i).forEach(this::registerPlantConceptInstance);
    }

    public void registerPlantConceptInstance(IPlantConcept i) {
        if (i == null) {
            return;
        }

        i.getAcceptedPlantMaterials().forEach(mat -> hashMapRetrieve.put(mat, i));
        materialsForSettings.addAll(i.getAcceptedSettingPlantMaterials());
    }

    public IPlantConcept retrieveSuitedPlantConcept(Material m) {
        return hashMapRetrieve.get(m);
    }

    public List<Material> retrieveAllSupportedMaterialsForSettings() {
        return materialsForSettings;
    }

    public List<Material> retrieveAllSupportedMaterials() {
        return new ArrayList<>(hashMapRetrieve.keySet());
    }

}
