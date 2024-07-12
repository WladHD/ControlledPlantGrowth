package de.wladtheninja.controlledplantgrowth.data;

import de.wladtheninja.controlledplantgrowth.data.dao.IPlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dao.ISettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dao.PlantBaseBlockDAO;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.ConfigDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
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
    private final ISettingsDAO<SettingsDTO, SettingsPlantGrowthDTO, ConfigDTO> settingsDataBase = new SettingsDAO();
}
