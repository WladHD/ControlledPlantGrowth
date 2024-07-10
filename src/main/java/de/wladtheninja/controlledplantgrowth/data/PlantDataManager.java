package de.wladtheninja.controlledplantgrowth.data;

import de.wladtheninja.controlledplantgrowth.data.dao.*;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantLocationChunkDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlantDataManager {
    @Getter(lazy = true)
    private final static PlantDataManager instance = new PlantDataManager();

    @Getter
    private final IPlantBaseBlockDAO<PlantBaseBlockDTO> plantDataBase = new PlantBaseBlockDAO();

    @Getter
    private final IPlantLocationChunkDAO<PlantLocationChunkDTO, PlantBaseBlockDTO> plantChunkDataBase = new PlantLocationChunkDAO();

    @Getter
    private final ISettingsDAO settingsDataBase = new SettingsDAO();
}
