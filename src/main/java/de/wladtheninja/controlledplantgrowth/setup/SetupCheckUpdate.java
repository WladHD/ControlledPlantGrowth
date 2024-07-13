package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.utils.GitHubVersionRetriever;
import de.wladtheninja.controlledplantgrowth.utils.SpigotVersionRetriever;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.logging.Level;

public class SetupCheckUpdate implements Runnable {

    @Override
    public void run() {
        boolean gitHub = PlantDataManager.getInstance()
                .getConfigDataBase()
                .getCurrentConfigFromCache()
                .isNotifyOnGitHubExperimentalRelease();

        boolean spigot = PlantDataManager.getInstance()
                .getConfigDataBase()
                .getCurrentConfigFromCache()
                .isNotifyOnSpigotRelease();

        if (!spigot && !gitHub) {
            return;
        }

        String installedVersionString = ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class)
                .getDescription()
                .getVersion();

        if (spigot) {
            try {
                new SpigotVersionRetriever(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                        ControlledPlantGrowth.SPIGOT_RESOURCE_ID).getVersion(version -> {

                    final String downloadUrl = "https://www.spigotmc.org/resources/controlled-plant-growth.117871/";
                    Bukkit.getLogger()
                            .log(Level.FINER,
                                    MessageFormat.format("Current version on SpigotMC: {0} (installed {1}) ({2})",
                                            version,
                                            installedVersionString,
                                            downloadUrl));

                    ComparableVersion release = new ComparableVersion(version);
                    ComparableVersion installed = new ComparableVersion(installedVersionString);

                    if (release.compareTo(installed) <= 0) { // <=
                        Bukkit.getLogger().log(Level.FINER, "Version is up to date (or newer) compared to SpigotMC");
                        return;
                    }


                    Bukkit.getLogger()
                            .log(Level.INFO,
                                    MessageFormat.format("\n{0}\n{1} has an update available on " +
                                                    "SpigotMC (available {2}, installed {3})\nDownload: {4}\n{0}",
                                            "-----------------------------",
                                            ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getName(),
                                            version,
                                            installed,
                                            downloadUrl));
                });
            }
            catch (Exception ex) {
                ControlledPlantGrowth.handleException(ex, Level.FINER);
            }
        }

        if (gitHub) {
            try {
                String downloadUrl = "https://github.com/WladHD/ControlledPlantGrowth";

                new GitHubVersionRetriever(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                        ControlledPlantGrowth.GITHUB_REPOSITORY_ID).getVersion(version -> {
                    Bukkit.getLogger()
                            .log(Level.FINER,
                                    MessageFormat.format("Current version on GitHub: {0} (installed {1}) ({2})",
                                            version,
                                            downloadUrl,
                                            installedVersionString));

                    ComparableVersion release = new ComparableVersion(version);
                    ComparableVersion installed = new ComparableVersion(installedVersionString);

                    if (release.compareTo(installed) <= 0) { // <=
                        Bukkit.getLogger().log(Level.FINER, "Version is up to date (or newer) compared to GitHub");
                        return;
                    }


                    Bukkit.getLogger()
                            .log(Level.INFO,
                                    MessageFormat.format("\n{0}\n{1} has an experimental update available on " +
                                                    "GitHub (available {2}, installed {3})\nDownload: {4}\n{0}",
                                            "-----------------------------",
                                            ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getName(),
                                            version,
                                            installed,
                                            downloadUrl));
                });
            }
            catch (Exception ex) {
                ControlledPlantGrowth.handleException(ex, Level.FINER);
            }
        }
    }


}
