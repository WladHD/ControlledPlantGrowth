package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.ControlledPlantGrowthCommandManager;
import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@PlantCommandData(name = "set",
                  permission = "controlledplantgrowth.set",
                  usage = "set <material> <time in seconds> [time unit]",
                  description =
                          "Sets the time of a specified plant to mature, saves the new config and applies the changes to " +
                                  "plants.")
public class ControlledPlantGrowthSetCommand
        implements IPlantCommandExecutor
{

    private final List<TimeUnit> acceptedTimeUnits = Arrays.asList(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS);

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args)
    {

        if (args.length < 3 || args.length > 4) {
            ControlledPlantGrowthCommandManager.getInstance()
                                               .getCommands()
                                               .forEach(cmd -> cmd.sendUsageInformation(sender, label));
            return true;
        }

        final List<Material> acceptedMats = ControlledPlantGrowthManager.getInstance()
                                                                        .retrieveAllSupportedMaterialsForSettings();

        String material = args[1].toUpperCase();
        String timeOrTimeArray = args[2];
        String timeType = args.length == 4 ?
                          args[3].toUpperCase() :
                          null;

        Material parsedMat;
        int parsedTime = -1;
        TimeUnit parsedTimeUnit = TimeUnit.MINUTES;
        List<Integer> parsedNonLinearTime = null;

        try {
            parsedMat = Material.valueOf(material);
        }
        catch (IllegalArgumentException e) {
            sender.sendMessage(MessageFormat.format("{0} is not a valid material. Check for typos.", material));
            return true;
        }

        if (!acceptedMats.contains(parsedMat)) {
            sender.sendMessage(MessageFormat.format(
                    "{0} is not a supported material. Supported materials are: {1}",
                    material,
                    Arrays.toString(acceptedMats.toArray())
            ));
            return true;
        }

        boolean useParsedTime = false;

        try {
            parsedTime = Integer.parseInt(timeOrTimeArray);
            useParsedTime = true;
        }
        catch (Exception ex) {
            parsedNonLinearTime = parseIntArray(timeOrTimeArray);

            if (parsedNonLinearTime == null) {
                sender.sendMessage(MessageFormat.format("{0} is not a number nor a correct array.", parsedTime));
                return true;
            }
        }

        if (timeType != null) {
            try {
                parsedTimeUnit = TimeUnit.valueOf(timeType);
            }
            catch (IllegalArgumentException e) {
                sender.sendMessage(MessageFormat.format(
                        "{0} is not a valid time unit. Supported time units are:",
                        Arrays.toString(acceptedTimeUnits.toArray())
                ));
                return true;
            }
        }

        SettingsDTO currentSettings = PlantDataManager.getInstance()
                                                      .getSettingsDataBase()
                                                      .getCurrentSettingsFromCache();

        Optional<SettingsPlantGrowthDTO> sdf = currentSettings.getPlantGrowthList()
                                                              .stream()
                                                              .filter(pgl -> pgl.getMaterial() == parsedMat)
                                                              .findFirst();

        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(parsedMat);

        int maxAgeIpc = ipc.getSettingsMaximalAge(parsedMat);

        if (!useParsedTime && parsedNonLinearTime.size() != ipc.getSettingsMaximalAge(parsedMat)) {
            sender.sendMessage(MessageFormat.format(
                    "{0} has {1} age levels and needs {1} (provided: {3}) array " + "entries:" + " f. e" + ". " + "{2}",
                    parsedMat,
                    maxAgeIpc,
                    Arrays.toString(IntStream.rangeClosed(1, maxAgeIpc).toArray()).replace(" ", ""),
                    parsedNonLinearTime.size()
            ));
            return true;
        }

        if (sdf.isPresent()) {
            sdf.get().setUseTimeForPlantMature(useParsedTime);

            if (useParsedTime) {
                sdf.get().setTimeForPlantMature((int) TimeUnit.SECONDS.convert(parsedTime, parsedTimeUnit));
            }
            else {
                sdf.get().setArray(parsedNonLinearTime, parsedTimeUnit);
            }

            PlantDataManager.getInstance().getSettingsDataBase().saveCachedCurrentSettings();

            sender.sendMessage(MessageFormat.format(
                    "Growth time for {0} was successfully updated to {1} {2}.",
                    parsedMat,
                    useParsedTime ?
                    parsedTime :
                    Arrays.toString(parsedNonLinearTime.toArray()).replace(" ", ""),
                    parsedTimeUnit
            ));

            ControlledPlantGrowthManager.getInstance()
                                        .getInternEventListener()
                                        .onForcePlantsReloadByDatabaseTypeEvent(parsedMat);
            return true;
        }

        SettingsPlantGrowthDTO settingsPlantGrowthDTO = new SettingsPlantGrowthDTO();
        settingsPlantGrowthDTO.setMaterial(parsedMat);

        settingsPlantGrowthDTO.setUseTimeForPlantMature(useParsedTime);

        if (useParsedTime) {
            settingsPlantGrowthDTO.setTimeForPlantMature((int) TimeUnit.SECONDS.convert(parsedTime, parsedTimeUnit));
        }
        else {
            settingsPlantGrowthDTO.setArray(parsedNonLinearTime, parsedTimeUnit);
        }

        PlantDataManager.getInstance()
                        .getSettingsDataBase()
                        .getCurrentSettingsFromCache()
                        .getPlantGrowthList()
                        .add(settingsPlantGrowthDTO);
        PlantDataManager.getInstance().getSettingsDataBase().saveCachedCurrentSettings();
        ControlledPlantGrowthManager.getInstance()
                                    .getInternEventListener()
                                    .onForcePlantsReloadByDatabaseTypeEvent(parsedMat);

        sender.sendMessage(MessageFormat.format(
                "Growth time for {0} was successfully updated to {1} {2}.",
                parsedMat,
                useParsedTime ?
                parsedTime :
                Arrays.toString(parsedNonLinearTime.toArray()).replace(" ", ""),
                parsedTimeUnit
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args)
    {

        final List<String> acceptedMats = getFilteredAcceptedMaterialsOnArg(args, 1);

        if (args.length <= 1) {
            return Collections.emptyList();
        }

        // args.length >= 2
        if (acceptedMats != null) {
            return acceptedMats;
        }

        // arg length 2 correct

        Material parsedMat = Material.valueOf(args[1].toUpperCase());
        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(parsedMat);

        if (args.length >= 3 && !isInteger(args[2]) && (parseIntArray(args[2]) == null ||
                parseIntArray(args[2]).size() != ipc.getSettingsMaximalAge(parsedMat)))
        {
            return Stream.of(
                    123,
                    Arrays.toString(IntStream.rangeClosed(1, ipc.getSettingsMaximalAge(parsedMat)).toArray())
                          .replace(" ", "")
            ).map(String::valueOf).collect(Collectors.toList());
        }


        if (args.length >= 4 && acceptedTimeUnits.stream().noneMatch(s -> s.toString().equalsIgnoreCase(args[3]))) {
            return acceptedTimeUnits.stream().map(Enum::toString).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public List<Integer> parseIntArray(String input) {
        try {

            input = input.substring(1, input.length() - 1);
            String[] stringNumbers = input.split(",");

            return Arrays.stream(stringNumbers).map(sn -> Integer.parseInt(sn.trim())).collect(Collectors.toList());
        }
        catch (Exception ignored) {
        }

        return new ArrayList<>();
    }


}
