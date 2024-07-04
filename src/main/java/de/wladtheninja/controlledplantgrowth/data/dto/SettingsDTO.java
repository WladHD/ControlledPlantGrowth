package de.wladtheninja.controlledplantgrowth.data.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SettingsDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active;

    @ElementCollection
    private List<SettingsPlantGrowthDTO> plantGrowthList;
}
