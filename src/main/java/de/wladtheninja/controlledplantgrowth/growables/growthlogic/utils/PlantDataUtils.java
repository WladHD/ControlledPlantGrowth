package de.wladtheninja.controlledplantgrowth.growables.growthlogic.utils;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlantDataUtils {

    public static Map.Entry<Integer, Long> calculateAgeAndNextUpdate(long currentTimeStamp,
                                                                     IPlantConceptBasic ipc,
                                                                     PlantBaseBlockDTO plant)
            throws PlantNoAgeableInterfaceException {
        final Block definitePlantRootBlock = plant.getLocation().getBlock();

        if (!ipc.containsAcceptedMaterial(definitePlantRootBlock.getType())) {
            return null;
        }

        if (ipc.isMature(definitePlantRootBlock)) {
            return new AbstractMap.SimpleEntry<>(-1, -1L);
        }

        boolean hasAge = ipc instanceof IPlantConceptAge;
        int realCurrentAge = hasAge ?
                ((IPlantConceptAge) ipc).getCurrentAge(plant.getLocation().getBlock()) :
                0;
        int realMaximumAge = hasAge ?
                ((IPlantConceptAge) ipc).getMaximumAge(plant.getLocation().getBlock()) :
                1;

        boolean hasPreviousTime = plant.getTimeNextGrowthStage() != -1;

        long previousUpdateTime = hasPreviousTime ?
                plant.getTimeNextGrowthStage() :
                currentTimeStamp;
        int tempSimulatedAge = realCurrentAge;

        SettingsPlantGrowthDTO settings = PlantDataManager.getInstance().getSettingsDataBase()
                .getPlantSettings(ipc.getDatabasePlantType(plant.getLocation().getBlock()));

        if (!settings.isUseTimeForPlantMature() &&
                settings.getTimeForNextPlantGrowthInSteps().size() != realMaximumAge) {
            Bukkit.getLogger()
                    .log(Level.WARNING,
                            MessageFormat.format(
                                    "Setting timeForNextPlantGrowthInSteps is set to true for {0}, yet the " +
                                            "length of the array is not matching the maximal age of the " + "plant" +
                                            ".\nYour array needs to have {1} numbers. E. g. {2}\n" +
                                            "(proceeding to use the sum of the array until its fixed)",
                                    settings.getMaterial(),
                                    realMaximumAge,
                                    IntStream.rangeClosed(1, realMaximumAge).boxed().collect(Collectors.toList())));
        }

        while (currentTimeStamp >= previousUpdateTime && tempSimulatedAge < realMaximumAge) {
            final long increaseBy = !settings.isUseTimeForPlantMature() &&
                    settings.getTimeForNextPlantGrowthInSteps().size() == realMaximumAge ?
                    (TimeUnit.MILLISECONDS.convert(settings.getTimeForNextPlantGrowthInSteps().get(tempSimulatedAge),
                            TimeUnit.SECONDS)) :
                    (TimeUnit.MILLISECONDS.convert(settings.getTimeForPlantMature(), TimeUnit.SECONDS)) /
                            realMaximumAge;

            previousUpdateTime += increaseBy;
            tempSimulatedAge += 1;

            Bukkit.getLogger()
                    .log(Level.FINER,
                            MessageFormat.format("{0} update step increased by {1}ms (AGE: {3}/{2} [{4}])",
                                    plant.getPlantType(),
                                    increaseBy,
                                    realMaximumAge,
                                    tempSimulatedAge,
                                    realCurrentAge));
        }

        if (hasPreviousTime && tempSimulatedAge >= realMaximumAge ||
                !hasPreviousTime && realCurrentAge >= realMaximumAge || previousUpdateTime < currentTimeStamp) {
            return new AbstractMap.SimpleEntry<>(-1, -1L);
        }

        return new AbstractMap.SimpleEntry<>(hasPreviousTime ?
                tempSimulatedAge :
                realCurrentAge, previousUpdateTime);
    }
}
