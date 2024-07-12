package de.wladtheninja.controlledplantgrowth.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@RequiredArgsConstructor
public class SetupSettings implements Runnable {
    public static void loadSettingsFromDatabase() {
        Bukkit.getLogger().log(Level.INFO, "Trying to load settings from database");

        // should really be only 0 or 1...
        List<SettingsDTO> activeSettings = PlantDataManager.getInstance().getSettingsDataBase().getAllActiveSettings();

        Bukkit.getLogger()
                .log(Level.FINER, MessageFormat.format("Found {0} active settings in database", activeSettings.size()));

        if (activeSettings.isEmpty()) {
            PlantDataManager.getInstance().getSettingsDataBase().saveSettings(getDefaultSettings());
            activeSettings = PlantDataManager.getInstance().getSettingsDataBase().getAllActiveSettings();
            Bukkit.getLogger().log(Level.FINER, "Restoring default settings");
        }

        PlantDataManager.getInstance().getSettingsDataBase().setCurrentSettings(activeSettings.getFirst());

        if (PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings() == null) {
            throw new RuntimeException("Could not find settings");
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Active settings with id {0} loaded successfully. Full config below: ",
                                PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings().getId()));

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Settings contain {0} records for plants",
                                PlantDataManager.getInstance()
                                        .getSettingsDataBase()
                                        .getCurrentSettings()
                                        .getPlantGrowthList()
                                        .size()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Bukkit.getLogger()
                .log(Level.FINER,
                        gson.toJson(PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings()));
    }

    public static SettingsDTO getDefaultSettings() {
        SettingsDTO defaultSettings = new SettingsDTO();

        defaultSettings.setMaximumAmountOfPlantsInATimeWindowCluster(1); // TODO remove... testing sanitization
        defaultSettings.setMaximumTimeWindowInMillisecondsForPlantsToBeClustered(1);
        defaultSettings.setActive(true);
        defaultSettings.setUseAggressiveChunkAnalysisAndLookForUnregisteredPlants(true);
        defaultSettings.setShowInfoWhenDefaultSettingIsUsed(false);

        ArrayList<SettingsPlantGrowthDTO> settingsPlantGrowths = new ArrayList<>();

        // grow wheat in 10 seconds
        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.WHEAT, false, 1).setArray(new int[]{
                3, 3, 3, 4, 2, 2, 1
        }, TimeUnit.SECONDS));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.BEETROOTS, true, min2sec(18)));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.POTATOES, true, 18));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.CARROTS, true, 18));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.NETHER_WART, true, 30));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.SWEET_BERRY_BUSH, true, 18));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.MELON_STEM, false, 1).setArray(new int[]{
                2, 2, 2, 4, 2, 2, 1, 3
        }, TimeUnit.MINUTES));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.PUMPKIN_STEM, false, 1).setArray(new int[]{
                2, 3, 1, 4, 2, 2, 1, 3
        }, TimeUnit.MINUTES));


        // AIR == default setting parsed when none is found
        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.AIR, true, 20));

        defaultSettings.setPlantGrowthList(settingsPlantGrowths);


        return defaultSettings;
    }

    public static int min2sec(int min) {
        return (int) TimeUnit.SECONDS.convert(min, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        if (PlantDataManager.getInstance().getSettingsDataBase().getCurrentConfig().isLoadConfigFromDatabase()) {
            loadSettingsFromDatabase();
            return;
        }

        PlantDataManager.getInstance().getSettingsDataBase().deleteAllActiveSettings();

        PlantDataManager.getInstance()
                .getSettingsDataBase()
                .setCurrentSettings(PlantDataManager.getInstance()
                        .getSettingsDataBase()
                        .getCurrentConfig()
                        .getPlantGrowthSettings());

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Settings imported from config.yml",
                                PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings().getId()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Bukkit.getLogger()
                .log(Level.FINER,
                        gson.toJson(PlantDataManager.getInstance().getSettingsDataBase().getCurrentSettings()));
    }
}
