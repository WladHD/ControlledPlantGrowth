package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dao.utils.ILoadLocalYML;

import java.io.File;

public interface IConfigDAO<T>
        extends ILoadLocalYML<T>
{

    void loadCurrentConfigAndCache();

    T getCurrentConfigFromCache();

    void saveCachedCurrentConfig();

    File getConfigPath();
}
