package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.ControlledPlantGrowthCommandManager;
import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@PlantCommandData(name = "set",
                  permission = "controlledplantgrowth.set",
                  usage = "set <material> <time in seconds> [time unit]",
                  description =
                          "Sets the time of a specified plant to mature, saves the new config and applies the changes to " +
                                  "plants.")
public class ControlledPlantGrowthSetCommand implements IPlantCommandExecutor {

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
                             String @NonNull [] args) {

        if (args.length < 3 || args.length > 4) {
            ControlledPlantGrowthCommandManager.getInstance()
                    .getCommands()
                    .forEach(cmd -> cmd.sendUsageInformation(sender, label));
            return false;
        }

        final List<Material> acceptedMats = ControlledPlantGrowthManager.getInstance()
                .retrieveAllSupportedMaterialsForSettings();

        String material = args[1].toUpperCase();
        String timeInSeconds = args[2];
        String timeType = args.length == 4 ?
                args[3].toUpperCase() :
                null;

        Material parsedMat;
        int parsedTime = -1;
        TimeUnit parsedTimeUnit = TimeUnit.MINUTES;

        try {
            parsedMat = Material.valueOf(material);
        }
        catch (IllegalArgumentException e) {
            sender.sendMessage(MessageFormat.format("{0} is not a valid material. Check for typos.", material));
            return true;
        }

        if (!acceptedMats.contains(parsedMat)) {
            sender.sendMessage(MessageFormat.format("{0} is not a supported material. Supported materials are: {1}",
                    material,
                    Arrays.toString(acceptedMats.toArray())));
            return true;
        }

        try {
            parsedTime = Integer.parseInt(timeInSeconds);
        }
        catch (Exception ex) {
            sender.sendMessage(MessageFormat.format("{0} is not a number.", parsedTime));
            return true;
        }

        if (timeType != null) {
            try {
                parsedTimeUnit = TimeUnit.valueOf(timeType);
            }
            catch (IllegalArgumentException e) {
                sender.sendMessage(MessageFormat.format("{0} is not a valid time unit. Supported time units are:",
                        Arrays.toString(acceptedTimeUnits.toArray())));
                return true;
            }
        }

        SettingsDTO currentSettings = PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings();

        Optional<SettingsPlantGrowthDTO> sdf = currentSettings.getPlantGrowthList()
                .stream()
                .filter(pgl -> pgl.getMaterial() == parsedMat)
                .findFirst();

        if (sdf.isPresent()) {
            sdf.get().setUseTimeForPlantMature(true);
            sdf.get().setTimeForPlantMature((int) TimeUnit.SECONDS.convert(parsedTime, parsedTimeUnit));
            PlantDataManager.getInstance().getSettingsDataBase().saveSettings(currentSettings);

            sender.sendMessage(MessageFormat.format("Growth time for {0} was successfully updated to {1} {2}.",
                    parsedMat,
                    parsedTime,
                    parsedTimeUnit));
            ControlledPlantGrowthManager.getInstance()
                    .getInternEventListener()
                    .onForcePlantsReloadByDatabaseTypeEvent(parsedMat);
            return true;
        }

        SettingsPlantGrowthDTO settingsPlantGrowthDTO = new SettingsPlantGrowthDTO();
        settingsPlantGrowthDTO.setTimeForPlantMature((int) TimeUnit.SECONDS.convert(parsedTime, parsedTimeUnit));
        settingsPlantGrowthDTO.setMaterial(parsedMat);
        settingsPlantGrowthDTO.setUseTimeForPlantMature(true);

        PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettings()
                .getPlantGrowthList()
                .add(settingsPlantGrowthDTO);
        PlantDataManager.getInstance().getSettingsDataBase().saveSettings(currentSettings);
        ControlledPlantGrowthManager.getInstance()
                .getInternEventListener()
                .onForcePlantsReloadByDatabaseTypeEvent(parsedMat);

        sender.sendMessage(MessageFormat.format("Growth time for {0} was successfully updated to {1} {2}.",
                parsedMat,
                parsedTime,
                parsedTimeUnit));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String[] args) {

        final List<String> acceptedMats = ControlledPlantGrowthManager.getInstance()
                .retrieveAllSupportedMaterialsForSettings()
                .stream()
                .map(Enum::toString)
                .collect(Collectors.toList());

        // /cmd set mat time
        if (args.length == 2 && acceptedMats.stream().noneMatch(s -> s.equalsIgnoreCase(args[1]))) {
            return acceptedMats;
        }

        // arg length 2 correct

        if (args.length == 3 && !isInteger(args[2])) {
            return Stream.of(60, 120, 300, 1800).map(String::valueOf).collect(Collectors.toList());
        }


        if (args.length == 4 && acceptedTimeUnits.stream().noneMatch(s -> s.toString().equalsIgnoreCase(args[3]))) {
            return acceptedTimeUnits.stream().map(Enum::toString).collect(Collectors.toList());
        }

        return null;
    }


}
