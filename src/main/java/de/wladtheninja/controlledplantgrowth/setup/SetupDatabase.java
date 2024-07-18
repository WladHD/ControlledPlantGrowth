package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateLocalPlantCacheUtil;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateSettingsUtil;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SetupDatabase
        implements Runnable
{
    @Override
    public void run() {
        Bukkit.getLogger().log(Level.FINER, "Setting up database for plant locations.");
        DatabaseHibernateLocalPlantCacheUtil.getInstance().setup();

        if (PlantDataManager.getInstance()
                            .getConfigDataBase()
                            .getCurrentConfigFromCache()
                            .isLoadPlantSettingsFromDatabase())
        {
            Bukkit.getLogger().log(Level.FINER, "Setting up database for plant settings.");
            DatabaseHibernateSettingsUtil.getInstance().setup();
        }
    }
}
