package de.wladtheninja.controlledplantgrowth.commands.cmds;

import de.wladtheninja.controlledplantgrowth.commands.IPlantCommandExecutor;
import de.wladtheninja.controlledplantgrowth.commands.PlantCommandData;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@PlantCommandData(name = "forceload",
                  permission = "controlledplantgrowth.forceload",
                  usage = "forceload",
                  description = "Search in all loaded chunks for plants to manage")
public class ControlledPlantGrowthForceloadCommand implements IPlantCommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String @NonNull [] args) {

        if (PlantDataManager.getInstance()
                .getSettingsDataBase()
                .getCurrentSettingsFromCache()
                .isUseAggressiveChunkAnalysisAndLookForUnregisteredPlants() &&
                (args.length != 2 || !args[1].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Plugin already checks all chunks on load due to the setting " +
                    "useAggressiveChunkAnalysisAndLookForUnregisteredPlants " + "in config is true.");
            sender.sendMessage("Rescan is most likely redundant. Do you still want to continue?");

            TextComponent message = new TextComponent(MessageFormat.format("/{0} forceload confirm", label));
            message.setColor(ChatColor.RED);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    MessageFormat.format("{0} forceload confirm", label)));

            sender.spigot().sendMessage(message);
            return true;
        }

        ControlledPlantGrowthManager.getInstance().getChunkAnalyser().clearChunkCache();
        AtomicInteger i = new AtomicInteger();
        Bukkit.getWorlds().forEach(w -> Arrays.stream(w.getLoadedChunks()).forEach(chunk -> {
            i.getAndIncrement();
            ControlledPlantGrowthManager.getInstance().getChunkAnalyser().onChunkLoaded(chunk);
        }));

        ControlledPlantGrowthManager.getInstance().getChunkAnalyser().notifyCommandSenderOnQueueFinish(sender);

        sender.sendMessage(MessageFormat.format(
                "All loaded chunks ({0}) successfully queued for scanning. You will receive a notification after all " +
                        "chunks have been analyzed. No further action is necessary.",
                i.get()));
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

        if (args.length >= 2 && !args[1].equals("confirm")) {
            return Collections.singletonList("confirm");
        }

        return new ArrayList<>();
    }
}
