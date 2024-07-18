package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.ControlledPlantGrowthCommandManager;
import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@PlantCommandData(name = "help",
                  permission = "controlledplantgrowth.help",
                  usage = "help",
                  description = "Prints out all commands of the ControlledPlantGrowth plugin")
public class ControlledPlantGrowthHelpCommand
        implements IPlantCommandExecutor
{
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args)
    {

        ControlledPlantGrowthCommandManager.getInstance()
                                           .getCommands()
                                           .forEach(cmd -> cmd.sendUsageInformation(sender, label));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args)
    {
        return Collections.emptyList();
    }
}
