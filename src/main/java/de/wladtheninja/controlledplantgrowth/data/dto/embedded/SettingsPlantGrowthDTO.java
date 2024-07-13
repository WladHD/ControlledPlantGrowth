package de.wladtheninja.controlledplantgrowth.data.dto.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Data
@Embeddable
@Getter
@Setter
@AllArgsConstructor()
public class SettingsPlantGrowthDTO {

    private Material material;
    private boolean useTimeForPlantMature;
    private Integer timeForPlantMature;
    private List<Integer> timeForNextPlantGrowthInSteps;

    public SettingsPlantGrowthDTO() {
        timeForNextPlantGrowthInSteps = new ArrayList<>();
    }

    public SettingsPlantGrowthDTO(Material mat, boolean useTime, int timeMatureInMinutes) {
        this(mat, useTime, timeMatureInMinutes, TimeUnit.MINUTES);
    }

    public SettingsPlantGrowthDTO(Material mat, boolean useTime, int timeMature, TimeUnit timeSource) {
        this();
        material = mat;
        useTimeForPlantMature = useTime;
        timeForPlantMature = (int) TimeUnit.SECONDS.convert(timeMature, timeSource);
    }

    @Transient
    public SettingsPlantGrowthDTO setArray(int[] numbers, TimeUnit sourceTime) {
        getTimeForNextPlantGrowthInSteps().clear();
        Arrays.stream(numbers)
                .map(number -> (int) TimeUnit.SECONDS.convert(number, sourceTime))
                .forEach(getTimeForNextPlantGrowthInSteps()::add);

        return this;
    }

    @Transient
    public SettingsPlantGrowthDTO setArray(List<Integer> numbers, TimeUnit sourceTime) {
        getTimeForNextPlantGrowthInSteps().clear();
        numbers.stream()
                .map(number -> (int) TimeUnit.SECONDS.convert(number, sourceTime))
                .forEach(getTimeForNextPlantGrowthInSteps()::add);

        return this;
    }

    @PostLoad
    @Transient
    public void postLoad() {
        Bukkit.getLogger().log(Level.FINER, "Post load triggered");
        // TODO other way is dependent on how many ageing steps a plant can take ... needs to be calculated elsewhere
        if (useTimeForPlantMature) {
            return;
        }

        timeForPlantMature = getTimeForNextPlantGrowthInSteps().stream().reduce(0, Integer::sum);
    }
}



