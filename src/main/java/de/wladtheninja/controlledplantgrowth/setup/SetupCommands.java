package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.commands.ControlledPlantGrowthCommandManager;
import de.wladtheninja.controlledplantgrowth.commands.cmds.ControlledPlantGrowthForceloadCommand;
import de.wladtheninja.controlledplantgrowth.commands.cmds.ControlledPlantGrowthHelpCommand;
import de.wladtheninja.controlledplantgrowth.commands.cmds.ControlledPlantGrowthInfoCommand;
import de.wladtheninja.controlledplantgrowth.commands.cmds.ControlledPlantGrowthSetCommand;
import org.bukkit.command.PluginCommand;

public class SetupCommands implements Runnable {
    @Override
    public void run() {
        ControlledPlantGrowthCommandManager mainCommandManager = ControlledPlantGrowthCommandManager.getInstance();

        mainCommandManager.registerCommand(new ControlledPlantGrowthHelpCommand());
        mainCommandManager.registerCommand(new ControlledPlantGrowthSetCommand());
        mainCommandManager.registerCommand(new ControlledPlantGrowthInfoCommand());
        mainCommandManager.registerCommand(new ControlledPlantGrowthForceloadCommand());

        final PluginCommand mainCommand =
                ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getCommand("controlledPlantGrowth");

        if (mainCommand == null) {
            throw new RuntimeException("Main command missing ...");
        }

        mainCommand.setExecutor(mainCommandManager);
        mainCommand.setTabCompleter(mainCommandManager);
    }
}
