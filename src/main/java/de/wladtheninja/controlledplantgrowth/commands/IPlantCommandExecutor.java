package de.wladtheninja.controlledplantgrowth.commands;

import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public interface IPlantCommandExecutor extends CommandExecutor, TabCompleter {

    default PlantCommandData getCommandData() {
        return getClass().getAnnotation(PlantCommandData.class);
    }

    default void sendUsageInformation(CommandSender sender, String label) {
        sender.spigot().sendMessage(getUsageInformationTextLabel(label));
    }

    default TextComponent getUsageInformationTextLabel(String label) {
        return new TextComponent(MessageFormat.format("{3}/{0} {4}{1} {5}=> {6}{2}",
                label,
                getCommandData().usage(),
                getCommandData().description(),
                ChatColor.GRAY,
                ChatColor.GREEN,
                ChatColor.DARK_GRAY,
                ChatColor.WHITE));
    }

    default List<String> getFilteredAcceptedMaterialsOnArg(String @NonNull [] args, int arg) {
        final List<String> acceptedMats = ControlledPlantGrowthManager.getInstance()
                .retrieveAllSupportedMaterialsForSettings()
                .stream()
                .map(Enum::toString)
                .collect(Collectors.toList());

        if (args.length >= 2 && acceptedMats.stream().noneMatch(s -> s.equalsIgnoreCase(args[1]))) {
            return acceptedMats.stream()
                    .filter(s -> s.startsWith(args[arg].trim().toUpperCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
	
