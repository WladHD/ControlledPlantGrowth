package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.utils.LoadLocalYML;
import de.wladtheninja.controlledplantgrowth.data.dto.external.ConfigDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.SettingsDTOVersion;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigDAO
        extends LoadLocalYML<ConfigDTO>
        implements IConfigDAO<ConfigDTO>
{
    private ConfigDTO currentConfig;

    public ConfigDAO() {
        super(ConfigDTO.class);
    }

    public static Map<String, String> getDefaultHibernateConfigLocalPlantCache() {
        HashMap<String, String> hib = new HashMap<>();

        hib.put("hibernate.connection.driver_class", "org.h2.Driver");
        hib.put(
                "hibernate.connection.url",
                MessageFormat.format(
                        "jdbc:h2:{0};AUTO_SERVER=TRUE",
                        "./plugins/ControlledPlantGrowth/data/plantCache"
                )
        );
        hib.put("hibernate.connection.username", "sa");
        hib.put("hibernate.connection.password", "");
        hib.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hib.put("hibernate.show_sql", "false");
        hib.put("hibernate.hbm2ddl.auto", "update");

        return hib;
    }

    public static Map<String, String> getDefaultHibernateConfigPlantSettings() {
        HashMap<String, String> hib = new HashMap<>();

        hib.put("hibernate.connection.driver_class", "org.h2.Driver");
        hib.put(
                "hibernate.connection.url",
                MessageFormat.format(
                        "jdbc:h2:{0};AUTO_SERVER=TRUE",
                        "./plugins/ControlledPlantGrowth/data" + "/plantSettings"
                )
        );
        hib.put("hibernate.connection.username", "sa");
        hib.put("hibernate.connection.password", "");
        hib.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hib.put("hibernate.show_sql", "false");
        hib.put("hibernate.hbm2ddl.auto", "update");

        return hib;
    }

    @Override
    public ConfigDTO getDefault() {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setEnableDebugLog(false);
        configDTO.setNotifyOnSpigotRelease(true);
        configDTO.setNotifyOnGitHubExperimentalRelease(false);
        configDTO.setActiveSettingsPage("default");
        configDTO.setCurrentSettingsVersion(SettingsDTOVersion.getCurrentVersion());
        configDTO.setHibernateConfigLocalPlantCache(getDefaultHibernateConfigLocalPlantCache());
        configDTO.setHibernateConfigPlantSettings(getDefaultHibernateConfigPlantSettings());

        return configDTO;
    }

    @Override
    public void loadCurrentConfigAndCache() {
        boolean veryOldVersion = false;

        try {
            currentConfig = loadYMLFile(getConfigPath());

            if (currentConfig == null || currentConfig.getCurrentSettingsVersion() == null) {
                veryOldVersion = true;
            }
        }
        catch (IOException ex) {
            ControlledPlantGrowth.handleException(ex);
            veryOldVersion = true;
        }

        if (veryOldVersion) {
            String msg = MessageFormat.format(
                    "Completely delete {0}. {1} was updated and can not use the old settings anymore.",
                    getConfigPath().getPath(),
                    ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getName()
            );
            Bukkit.getLogger().log(Level.SEVERE, msg);

            ControlledPlantGrowth.handleException(null, Level.SEVERE);
        }
    }

    @Override
    public ConfigDTO getCurrentConfigFromCache() {
        return currentConfig;
    }

    @Override
    public void saveCachedCurrentConfig() {
        try {
            saveYMLFile(getCurrentConfigFromCache(), getConfigPath());
        }
        catch (IOException ex) {
            ControlledPlantGrowth.handleException(ex);
        }

        loadCurrentConfigAndCache();
    }

    @Override
    public File getConfigPath() {
        File fc = ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getDataFolder();

        return new File(fc, "config.yml");
    }
}
