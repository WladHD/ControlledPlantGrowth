package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthInformation;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlantDataUtils {

    private static final boolean _CALCULATE_LINEAR = true; // TODO relocate to settings

    public static int determineCurrentPlantAge(IPlantConcept ipc,
                                               Block b) {
        IPlantConceptGrowthInformation conceptGrowthInformation = ipc instanceof IPlantConceptGrowthInformation ?
                (IPlantConceptGrowthInformation) ipc :
                null;

        IPlantConceptAge conceptAge = ipc instanceof IPlantConceptAge ?
                (IPlantConceptAge) ipc :
                null;

        if (conceptGrowthInformation == null) {
            throw new RuntimeException("Plant does not have any levels of age ...");
        }

        if (conceptGrowthInformation.isMature(b)) {
            return -1;
        }

        return conceptAge == null ?
                0 :
                conceptAge.getCurrentAge(b);
    }

    // TODO startingTime ... maybe allow plants that were planted in the past (f. e. pre server stop) to grow
    //  instantly ...
    public static long determineTimestampForNextUpdate(SettingsPlantGrowthDTO settings,
                                                       long startingTime,
                                                       int currentAge,
                                                       int maxAge) {
        if (maxAge <= 1) {
            return settings.getTimeForPlantMature() * 1000 + startingTime;
        }

        if (maxAge <= currentAge) {
            return -1;
        }

        // TODO
        if (!settings.isUseTimeForPlantMature()) {
            if (settings.getTimeForNextPlantGrowthInSteps().length == maxAge) {
                return settings.getTimeForNextPlantGrowthInSteps()[currentAge] * 1000L + startingTime;
            }

            Bukkit.getLogger()
                    .log(Level.WARNING,
                         MessageFormat.format("Setting timeForNextPlantGrowthInSteps is set to true for {0}, yet the " +
                                                      "length of the array is not matching the maximal age of the " +
                                                      "plant" + ".\nYour array needs to have {1} numbers. E. g. {2}\n" +
                                                      "(proceeding to use " +
                                                      "the sum of the array until its fixed, current time until " +
                                                      "mature: {3}s " + "remaining [from {4}s])",
                                              settings.getMaterial(),
                                              maxAge,
                                              IntStream.rangeClosed(1, maxAge).boxed().collect(Collectors.toList()),
                                              Math.round(settings.getTimeForPlantMature() *
                                                                 ((double) currentAge / maxAge)),
                                              settings.getTimeForPlantMature()));
        }

        return (settings.getTimeForPlantMature() * 1000L) / maxAge + startingTime;
    }

    public static void fillPlantBaseBlockDTOWithCurrentAgeAndNextUpdateTimestamp(IPlantConcept ipc,
                                                                                 PlantBaseBlockDTO pbb) {
        pbb.setCurrentPlantStage(determineCurrentPlantAge(ipc, pbb.getLocation().getBlock()));

        if (pbb.getCurrentPlantStage() == -1) {
            pbb.setTimeNextGrowthStage(-1);
            return;
        }

        final long currentTime = System.currentTimeMillis();
        final int maxAge = ipc instanceof IPlantConceptAge ?
                ((IPlantConceptAge) ipc).getMaximumAge(pbb.getLocation().getBlock()) :
                1;
        final SettingsPlantGrowthDTO settings =
                SettingsDAO.getInstance().getPlantSettings(pbb.getLocation().getBlock().getType());

        pbb.setTimeNextGrowthStage(determineTimestampForNextUpdate(settings,
                                                                   currentTime,
                                                                   pbb.getCurrentPlantStage(),
                                                                   maxAge));
    }

}
