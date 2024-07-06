package de.wladtheninja.controlledplantgrowth.data.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "SettingsDTO")
public class SettingsDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active;

    private boolean disableNaturalGrowth;

    private boolean showInfoWhenDefaultSettingIsUsed;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SettingsPlantGrowthDTO> plantGrowthList;

    private int maximumAmountOfPlantsInATimeWindowCluster;

    private int maximumTimeWindowInMillisecondsForPlantsToBeClustered;
}
