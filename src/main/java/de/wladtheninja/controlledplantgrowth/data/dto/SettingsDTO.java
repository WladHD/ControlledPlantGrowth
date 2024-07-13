package de.wladtheninja.controlledplantgrowth.data.dto;

import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.SettingsDTOVersion;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "SettingsDTO")
public class SettingsDTO implements Serializable {

    @Id
    private String settingsPageName;

    private SettingsDTOVersion settingsVersion = SettingsDTOVersion.getCurrentVersion();

    private boolean disableNaturalGrowth;

    private boolean showInfoWhenDefaultSettingIsUsed;

    private boolean useAggressiveChunkAnalysisAndLookForUnregisteredPlants;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SettingsPlantGrowthDTO> plantGrowthList;

    private int maximumAmountOfPlantsInATimeWindowCluster;

    private int maximumTimeWindowInMillisecondsForPlantsToBeClustered;
}
