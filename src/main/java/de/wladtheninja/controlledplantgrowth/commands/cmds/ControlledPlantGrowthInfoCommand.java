package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@PlantCommandData(name = "info",
                  onlyPlayerCMD = false,
                  permission = "controlledplantgrowth.info",
                  usage = "info",
                  description = "Prints out all growth timers set by the ControlledPlantGrowth plugin")
public class ControlledPlantGrowthInfoCommand implements IPlantCommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args) {

        PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .getPlantGrowthList()
                .forEach(pgl -> sender.sendMessage(MessageFormat.format("{0} - {1}s",
                        pgl.getMaterial(),
                        pgl.isUseTimeForPlantMature() ?
                                pgl.getTimeForPlantMature() :
                                Arrays.toString(pgl.getTimeForNextPlantGrowthInSteps().toArray()))));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args) {

        if (!PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants()) {
            return new ArrayList<>();
        }

        if (args.length == 2 && !args[1].equals("confirm")) {
            return Collections.singletonList("confirm");
        }

        return new ArrayList<>();
    }
}
