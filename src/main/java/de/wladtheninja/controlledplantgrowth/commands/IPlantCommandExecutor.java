package de.wladtheninja.controlledplantgrowth.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.text.MessageFormat;

public interface IPlantCommandExecutor extends CommandExecutor, TabCompleter {

	default PlantCommandData getCommandData() {
		return getClass().getAnnotation(PlantCommandData.class);
	}

	default void sendUsageInformation(CommandSender sender,
									  String label) {
		sender.sendMessage(getUsageInformation(label));
	}

	default String getUsageInformation(String label) {
		return MessageFormat.format("/{0} {1} => {2}", label, getCommandData().usage(),
									getCommandData().description());
	}
}
	
