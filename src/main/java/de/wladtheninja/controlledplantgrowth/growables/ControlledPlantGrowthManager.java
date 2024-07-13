package de.wladtheninja.controlledplantgrowth.growables;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.*;


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
    private final HashMap<Material, IPlantConceptBasic> hashMapRetrieve = new HashMap<>();

    @Getter
    private final List<Material> materialsForSettings = new ArrayList<>();

    public void registerPlantConceptInstance(IPlantConceptBasic... i) {
        Arrays.stream(i).forEach(this::registerPlantConceptInstance);
    }

    public void registerPlantConceptInstance(IPlantConceptBasic i) {
        if (i == null) {
            return;
        }

        Optional<SettingsPlantGrowthDTO> settingsDTO = i.getAcceptedSettingPlantMaterials()
                .stream()
                .map(fl -> PlantDataManager.getInstance().getSettingsDataBase().getPlantSettings(fl))
                .filter(Objects::nonNull)
                .findFirst();

        if (!settingsDTO.isPresent() || settingsDTO.get().getMaterial() == Material.AIR) {
            return;
        }


        i.getAcceptedPlantMaterials().forEach(mat -> hashMapRetrieve.put(mat, i));
        materialsForSettings.addAll(i.getAcceptedSettingPlantMaterials());
    }

    public IPlantConceptBasic retrieveSuitedPlantConcept(Material m) {
        return hashMapRetrieve.get(m);
    }

    public List<Material> retrieveAllSupportedMaterialsForSettings() {
        return materialsForSettings;
    }

    public List<Material> retrieveAllSupportedMaterials() {
        return new ArrayList<>(hashMapRetrieve.keySet());
    }

    /* CHECKLIST (see https://minecraft.fandom.com/wiki/Crops)
    Wheat Seeds             Y
    Beetroot Seeds          Y
    Carrot                  Y
    Potato                  Y
    Melon                   Y
    Pumpkin                 Y
    Torchflower Seeds       X?
    Pitcher Pod             X?
    Bamboo                  X
    Cocoa Beans             X
    Sugar Cane              Y
    Sweet Berries           Y
    Cactus                  Y
    Mushrooms               X?
    Kelp                    X
    Sea Pickle              X?
    Nether Wart             Y
    Chorus Fruit            X?
    Fungus                  X?
    Glow Berries            X?
    Saplings                X
     */
}
