package de.wladtheninja.controlledplantgrowth.data;

import de.wladtheninja.controlledplantgrowth.data.dao.*;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.external.ConfigDTO;
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
    private final ISettingsDAO<SettingsDTO, SettingsPlantGrowthDTO> settingsDataBase = new SettingsDAO();

    @Getter
    private final IConfigDAO<ConfigDTO> configDataBase = new ConfigDAO();
}
