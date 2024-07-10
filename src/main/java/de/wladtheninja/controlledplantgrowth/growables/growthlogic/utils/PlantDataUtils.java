package de.wladtheninja.controlledplantgrowth.growables.growthlogic.utils;

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
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlantDataUtils {

    public static Map.Entry<Integer, Long> calculateAgeAndNextUpdate(long timeStamp,
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


        long previousUpdateTime = plant.getTimeNextGrowthStage() == -1 ?
                timeStamp :
                plant.getTimeNextGrowthStage();
        int previousAge = realCurrentAge;

        SettingsPlantGrowthDTO settings = SettingsDAO.getInstance()
                .getPlantSettings(ipc.getDatabasePlantType(plant.getLocation().getBlock()));

        if (!settings.isUseTimeForPlantMature() &&
                settings.getTimeForNextPlantGrowthInSteps().length != realMaximumAge) {
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

        while (timeStamp >= previousUpdateTime && realMaximumAge > previousAge) {
            previousUpdateTime += settings.isUseTimeForPlantMature() &&
                    settings.getTimeForNextPlantGrowthInSteps().length == realMaximumAge ?
                    (settings.getTimeForNextPlantGrowthInSteps()[previousAge] * 1000L) :
                    (settings.getTimeForPlantMature() * 1000L) / realMaximumAge;

            if (timeStamp >= previousUpdateTime) {
                previousAge += 1;
            }

            Bukkit.getLogger().log(Level.FINER, MessageFormat.format("{0} {1}", timeStamp, previousUpdateTime));
        }

        if (previousAge == realMaximumAge) {
            return new AbstractMap.SimpleEntry<>(-1, -1L);
        }

        return new AbstractMap.SimpleEntry<>(previousAge, previousUpdateTime);
    }
}
