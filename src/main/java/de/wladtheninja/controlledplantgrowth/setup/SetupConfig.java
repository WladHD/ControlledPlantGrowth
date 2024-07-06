package de.wladtheninja.controlledplantgrowth.setup;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.ConfigDTO;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SetupConfig implements Runnable {
    public static void loadConfig(File configFile)
            throws IOException {
        YAMLMapper yamlMapper = new YAMLMapper().configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
        ConfigDTO loadedConfig = yamlMapper.readValue(configFile, ConfigDTO.class);
        SettingsDAO.getInstance().setCurrentConfig(loadedConfig);
    }

    public static void createDefaultConfig(File pluginDirectory)
            throws IOException {
        Bukkit.getLogger().log(Level.FINER, "Config.yml does not exist, creating one.");

        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setEnableDebugLog(true);
        configDTO.setLoadConfigFromDatabase(false);
        configDTO.setPlantGrowthSettings(SetupSettings.getDefaultSettings());
        configDTO.setDatabaseHibernateSettings(getDefaultHibernateSettings(pluginDirectory));

        saveConfig(configDTO, pluginDirectory);
    }

    public static void saveConfig(ConfigDTO configDTO,
                                  File pluginDirectory)
            throws IOException {
        YAMLMapper yamlMapper = new YAMLMapper().configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);

        File f = new File(pluginDirectory, "config.yml");

        if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
            throw new RuntimeException("Could not create plugin folder.");
        }

        yamlMapper.writeValue(f, configDTO);
    }

    public static Map<String, String> getDefaultHibernateSettings(File pluginDirectory) {
        HashMap<String, String> hib = new HashMap<>();

        hib.put("hibernate.connection.driver_class", "org.h2.Driver");
        hib.put("hibernate.connection.url",
                MessageFormat.format("jdbc:h2:{0};AUTO_SERVER=TRUE", "./plugins/ControlledPlantGrowth/data/db"));
        hib.put("hibernate.connection.username", "sa");
        hib.put("hibernate.connection.password", "");
        hib.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hib.put("hibernate.show_sql", "true"); // TODO REMOVE
        hib.put("hibernate.hbm2ddl.auto", "update");

        return hib;
    }

    @Override
    public void run() {
        File fc = ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getDataFolder();
        File configFile = new File(fc, "config.yml");

        if (!configFile.exists()) {
            try {
                createDefaultConfig(fc);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }

        try {
            loadConfig(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
