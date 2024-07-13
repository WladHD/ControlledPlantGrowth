package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dao.utils.ILoadLocalYML;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import org.bukkit.Material;

import java.io.File;

public interface ISettingsDAO<T, S> extends ILoadLocalYML<T> {

    SettingsDTO getSettingPageByName(String name);

    S getPlantSettings(Material mat);

    void loadCurrentSettingsAndCache();

    T getCurrentSettingsFromCache();

    void saveCachedCurrentSettings();

    File getSettingsPath();

}
