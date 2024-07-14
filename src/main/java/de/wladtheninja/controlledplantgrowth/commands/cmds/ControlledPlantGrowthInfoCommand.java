package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PlantCommandData(name = "info",
                  permission = "controlledplantgrowth.info",
                  usage = "info [material]",
                  description = "Prints out all growth timers set by the ControlledPlantGrowth plugin")
public class ControlledPlantGrowthInfoCommand implements IPlantCommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args) {

        Material focus;

        if (args.length >= 2) {
            if (Material.getMaterial(args[1].trim().toUpperCase()) == null) {
                sender.sendMessage(MessageFormat.format("{0} is not a valid material.", args[1]));
                return true;
            }

            focus = Material.getMaterial(args[1].trim().toUpperCase());

            if (!PlantDataManager.getInstance().getSettingsDataBase().hasPlantSetting(focus)) {
                sender.sendMessage(MessageFormat.format("{0} is not configured in the settings. Add to plantSettings" +
                                ".yml if it is supported by {1}.",
                        args[1],
                        ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getName()));

                return true;
            }

            if (ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(focus) == null) {
                sender.sendMessage(MessageFormat.format("{0} is not supported by {1}.",
                        args[1],
                        ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getName()));
                return true;
            }

        }
        else {
            focus = null;
        }

        List<SettingsPlantGrowthDTO> settings = PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .getPlantGrowthList();

        if (focus != null) {
            settings = settings.stream().filter(setting -> setting.getMaterial() == focus).collect(Collectors.toList());
        }


        settings.forEach(pgl -> sender.sendMessage(MessageFormat.format("{0} - {1}s",
                pgl.getMaterial(),
                pgl.isUseTimeForPlantMature() ?
                        pgl.getTimeForPlantMature() == null ?
                                1 :
                                pgl.getTimeForPlantMature() :
                        Arrays.toString(pgl.getTimeForNextPlantGrowthInSteps().toArray()))));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args) {

        final List<String> acceptedMats1 = getFilteredAcceptedMaterialsOnArg(args, 1);
        if (args.length >= 2 && acceptedMats1 != null) {
            return acceptedMats1;
        }

        return Collections.emptyList();
    }
}
