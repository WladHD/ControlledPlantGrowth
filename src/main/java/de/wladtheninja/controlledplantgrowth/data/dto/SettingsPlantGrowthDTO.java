package de.wladtheninja.controlledplantgrowth.data.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.bukkit.Material;

@Data
@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class SettingsPlantGrowthDTO {

    private Material material;
    private boolean useTimeForPlantMature;

    @Column(nullable = true)
    private Integer timeForPlantMature;

    @Column(nullable = false)
    private int @NonNull [] timeForNextPlantGrowthInSteps;


    public SettingsPlantGrowthDTO() {

    }
}



