package de.wladtheninja.controlledplantgrowth.commands;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControlledPlantGrowthCommandManager
        implements CommandExecutor, TabCompleter
{

    @Getter(lazy = true)
    private static final ControlledPlantGrowthCommandManager instance = new ControlledPlantGrowthCommandManager();

    @Getter
    private final List<IPlantCommandExecutor> commands;

    private ControlledPlantGrowthCommandManager() {
        commands = new ArrayList<>();
    }

    public void registerCommand(IPlantCommandExecutor commandClass) {
        if (commandClass == null || commands.stream()
                                            .anyMatch(cmd -> cmd.getCommandData()
                                                                .name()
                                                                .equalsIgnoreCase(commandClass.getCommandData()
                                                                                              .name())))
        {
            return;
        }

        commands.add(commandClass);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args)
    {

        if (args.length == 0) {
            return onCommand(sender, command, label, new String[]{"help"});
        }

        final String cmd = args[0];

        Optional<IPlantCommandExecutor> parsedCmd = getCommands().stream()
                                                                 .filter(commandClass -> commandClass.getCommandData()
                                                                                                     .name()
                                                                                                     .equalsIgnoreCase(
                                                                                                             cmd))
                                                                 .findFirst();

        if (!parsedCmd.isPresent()) {
            sender.sendMessage(MessageFormat.format(
                    "Command /{0} {1} was not found. Printing help menu:",
                    label,
                    args[0]
            ));
            onCommand(sender, command, label, new String[]{"help"});
            return true;
        }

        IPlantCommandExecutor executor = parsedCmd.get();

        if (!executor.getCommandData().permission().isEmpty() &&
                !sender.hasPermission(executor.getCommandData().permission()))
        {
            sender.sendMessage(MessageFormat.format(
                    "This command requires the permission ''{0}''.",
                    executor.getCommandData().permission()
            ));
            return true;
        }

        return parsedCmd.get().onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String label,
                                      String @NonNull [] args)
    {

        final List<String> defaultCommands = getCommands().stream()
                                                          .filter(cmd -> cmd.getCommandData().permission().isEmpty() ||
                                                                  sender.hasPermission(cmd.getCommandData()
                                                                                          .permission()))
                                                          .map(cmd -> cmd.getCommandData().name().toLowerCase())
                                                          .collect(Collectors.toList());

        if (args.length == 0) {
            return defaultCommands;
        }

        if (defaultCommands.stream().noneMatch(dc -> dc.equalsIgnoreCase(args[0].trim().toLowerCase()))) {
            return defaultCommands.stream()
                                  .filter(dc -> dc.startsWith(args[0].trim().toLowerCase()))
                                  .collect(Collectors.toList());
        }

        final String cmd = args[0];

        Optional<IPlantCommandExecutor> parsedCmd = getCommands().stream()
                                                                 .filter(commandClass -> commandClass.getCommandData()
                                                                                                     .name()
                                                                                                     .equalsIgnoreCase(
                                                                                                             cmd))
                                                                 .findFirst();

        return parsedCmd.map(iPlantCommandExecutor -> iPlantCommandExecutor.onTabComplete(sender, command, label, args))
                        .orElse(defaultCommands);

    }
}
