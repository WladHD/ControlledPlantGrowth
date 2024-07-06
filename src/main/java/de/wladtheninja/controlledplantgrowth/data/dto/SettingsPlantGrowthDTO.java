package de.wladtheninja.controlledplantgrowth.data.dto;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JacksonInject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.logging.Level;
import java.util.stream.IntStream;

@Data
@Embeddable
@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor()
public class SettingsPlantGrowthDTO {

    private Material material;
    private boolean useTimeForPlantMature;

    @Column(nullable = true)
    private Integer timeForPlantMature;

    @Column(nullable = false)
    private int @NonNull [] timeForNextPlantGrowthInSteps;

    @PostLoad
    @Transient
    public void postLoad() {
        Bukkit.getLogger().log(Level.FINER, "Post load triggered");
        // TODO other way is dependent on how many ageing steps a plant can take ... needs to be calculated elsewhere
        if (useTimeForPlantMature) {
            return;
        }

        timeForPlantMature = IntStream.of(timeForNextPlantGrowthInSteps).sum();
    }
}



