package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import org.bukkit.Material;

import java.util.List;

public interface ISettingsDAO<T, S, Q> {

    T getCurrentSettings();

    void setCurrentSettings(T settings);

    List<T> getAllActiveSettings();

    void deleteAllActiveSettings();

    S getPlantSettings(Material mat);

    Q getCurrentConfig();

    void setCurrentConfig(Q config);

    void saveSettings(SettingsDTO settingsDTO);

}
