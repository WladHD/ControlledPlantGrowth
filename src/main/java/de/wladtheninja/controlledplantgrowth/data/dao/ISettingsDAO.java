package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dao.err.PlantSettingNotFoundException;
import de.wladtheninja.controlledplantgrowth.data.dao.utils.ILoadLocalYML;
import lombok.NonNull;
import org.bukkit.Material;

import java.io.File;

public interface ISettingsDAO<T, S> extends ILoadLocalYML<T> {

    T getSettingPageByName(String name);

    @NonNull
    S getPlantSettings(Material mat) throws PlantSettingNotFoundException;

    default S getPlantSettingNullable(Material mat) {
        try {
            return getPlantSettings(mat);
        }
        catch (PlantSettingNotFoundException e) {
            return null;
        }
    }

    default boolean hasPlantSetting(Material mat) {
        try {
            getPlantSettings(mat);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    void loadCurrentSettingsAndCache();

    T getCurrentSettingsFromCache();

    void saveCachedCurrentSettings();

    File getSettingsPath();

}
