package de.wladtheninja.controlledplantgrowth.utils;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;

import java.text.MessageFormat;
import java.util.function.Consumer;

@AllArgsConstructor
// Inspiration taken from https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class GitHubVersionRetriever {

    private final JavaPlugin plugin;
    private final long repositoryId;

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                GitHub github = GitHub.connectAnonymously();
                GHRelease latestRelease = github.getRepositoryById(repositoryId).getLatestRelease();

                if (latestRelease.getTagName() != null) {
                    consumer.accept(latestRelease.getTagName());
                }
            }
            catch (Exception e) {
                plugin.getLogger()
                      .info(MessageFormat.format("Unable to check for updates on GitHub: {0}", e.getMessage()));
            }
        });
    }
}
