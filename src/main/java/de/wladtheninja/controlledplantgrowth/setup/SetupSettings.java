package de.wladtheninja.controlledplantgrowth.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@RequiredArgsConstructor
public class SetupSettings implements Runnable {
    @Override
    public void run() {
        Bukkit.getLogger().log(Level.FINER, "Trying to load settings from database");

        // should really be only 0 or 1...
        List<SettingsDTO> activeSettings = SettingsDAO.getInstance().getAllActiveSettings();

        Bukkit.getLogger()
                .log(Level.FINER, MessageFormat.format("Found {0} active settings in database", activeSettings.size()));

        if (activeSettings.size() == 0) {
            SettingsDAO.getInstance().saveSettings(getDefaultSettings());
            activeSettings = SettingsDAO.getInstance().getAllActiveSettings();
            Bukkit.getLogger().log(Level.FINER, "Restoring default settings");
        }

        SettingsDAO.getInstance().setCurrentSettings(activeSettings.getFirst());

        if (SettingsDAO.getInstance().getCurrentSettings() == null) {
            throw new RuntimeException("Could not find settings");
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Active settings with id {0} loaded successfully. Full config below: ",
                                          SettingsDAO.getInstance().getCurrentSettings().getId()));

        Bukkit.getLogger()
                .log(Level.FINER,
                     MessageFormat.format("Settings contain {0} records for plants",
                                          SettingsDAO.getInstance().getCurrentSettings().getPlantGrowthList().size()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Bukkit.getLogger().log(Level.FINER, gson.toJson(SettingsDAO.getInstance().getCurrentSettings()));
    }

    public SettingsDTO getDefaultSettings() {
        SettingsDTO defaultSettings = new SettingsDTO();

        defaultSettings.setActive(true);

        ArrayList<SettingsPlantGrowthDTO> settingsPlantGrowths = new ArrayList<>();

        // grow wheat in 10 seconds
        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.WHEAT, true, 10, new int[0]));

        defaultSettings.setPlantGrowthList(settingsPlantGrowths);

        return defaultSettings;
    }
}
