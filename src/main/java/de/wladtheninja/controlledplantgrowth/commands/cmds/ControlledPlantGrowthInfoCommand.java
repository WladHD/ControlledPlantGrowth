package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
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

        SettingsDAO.getInstance()
                .getCurrentSettings()
                .getPlantGrowthList()
                .forEach(pgl -> sender.sendMessage(MessageFormat.format("{1} - {2}s",
                                                                        pgl.getMaterial(),
                                                                        pgl.isUseTimeForPlantMature() ?
                                                                                pgl.getTimeForPlantMature() :
                                                                                Arrays.toString(pgl.getTimeForNextPlantGrowthInSteps()))));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args) {
        return null;
    }
}