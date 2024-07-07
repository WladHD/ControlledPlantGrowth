package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@PlantCommandData(name = "forceload",
        onlyPlayerCMD = false,
        permission = "controlledplantgrowth.forceload",
        usage = "forceload",
        description = "Search in all loaded chunks for plants to manage")
public class ControlledPlantGrowthForceloadCommand implements IPlantCommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args) {

        if (SettingsDAO.getInstance().getCurrentSettings().isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants() &&
                args.length != 2 && !args[1].equalsIgnoreCase("confirm")) {
            sender.sendMessage("Plugin already checks all chunks on load due to the setting " +
                                       "useAggressiveChunkAnalysisAndLookForUnregisteredPlants " +
                                       "in config is true.");
            sender.sendMessage(
                    "Rescan is most likely redundant. Do you still want to continue? Then click the command below or " +
                            "type it.");

            TextComponent message = new TextComponent(MessageFormat.format("/{0} forceload confirm", label));
            message.setColor(ChatColor.RED);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                 MessageFormat.format("{0} forceload confirm", label)));
            return false;
        }

        Bukkit.getWorlds()
                .forEach(w -> Arrays.stream(w.getLoadedChunks())
                        .forEach(chunk -> ControlledPlantGrowthManager.getInstance()
                                .getChunkAnalyser()
                                .checkForPlantsInChunk(chunk)));

        sender.sendMessage("All loaded chunks successfully queued for scanning. No further action is required.");
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
