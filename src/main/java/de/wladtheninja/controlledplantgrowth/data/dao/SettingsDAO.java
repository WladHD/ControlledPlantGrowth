package de.wladtheninja.controlledplantgrowth.data.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dao.utils.LoadLocalYML;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateSettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SettingsDAO extends LoadLocalYML<SettingsDTO>
        implements ISettingsDAO<SettingsDTO, SettingsPlantGrowthDTO> {

    private SettingsDTO cachedCurrentSettings;

    public SettingsDAO() {
        super(SettingsDTO.class);
    }

    private static boolean isUsingDB() {
        return PlantDataManager.getInstance()
                .getConfigDataBase()
                .getCurrentConfigFromCache()
                .isLoadPlantSettingsFromDatabase();
    }

    private static String getConfigSettingsPageName() {
        return PlantDataManager.getInstance().getConfigDataBase().getCurrentConfigFromCache().getActiveSettingsPage();
    }

    @Override
    public SettingsDTO getSettingPageByName(String name) {
        List<SettingsDTO> activeSettings = null;

        try (Session session = DatabaseHibernateSettingsUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM SettingsDTO WHERE settingsPageName = :name";
            Query<SettingsDTO> query = session.createQuery(hql, SettingsDTO.class);
            query.setParameter("name", name);
            activeSettings = query.getResultList();

            transaction.commit();
        }
        catch (Exception exception) {
            ControlledPlantGrowth.handleException(exception);
        }

        if (activeSettings == null) {
            return null;
        }

        return activeSettings.stream().findFirst().orElse(null);
    }

    public SettingsPlantGrowthDTO getPlantSettings(Material m) {
        final SettingsPlantGrowthDTO s = getCurrentSettingsFromCache().getPlantGrowthList()
                .stream()
                .filter(entry -> entry.getMaterial() == m)
                .findFirst()
                .orElse(null);


        if (s != null) {
            return s;
        }

        if (getCurrentSettingsFromCache().isShowInfoWhenDefaultSettingIsUsed()) {
            Bukkit.getLogger()
                    .log(Level.INFO,
                            MessageFormat.format(
                                    "Plant {0} had no rules for growing defined. Using the default rules " +
                                            "instead. If this behaviour is wanted, ignore this message or " +
                                            "turn the setting showInfoWhenDefaultSettingIsUsed to false.", m));
        }

        return getCurrentSettingsFromCache().getPlantGrowthList()
                .stream()
                .filter(entry -> entry.getMaterial() == Material.AIR)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Settings may be corrupted. Could not locate the default settings."));
    }

    @Override
    public void loadCurrentSettingsAndCache() {
        if (isUsingDB()) {
            Bukkit.getLogger().log(Level.INFO, "Loading current settings from database ...");
            loadCurrentSettingsAndCacheFromDatabase();
        }
        else {
            Bukkit.getLogger().log(Level.INFO, "Loading current settings from file ...");
            loadCurrentSettingsAndCacheFromFile();
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Loaded settings ''{0}'' v{1} contain {2} records for plants",
                                getCurrentSettingsFromCache().getSettingsPageName(),
                                getCurrentSettingsFromCache().getSettingsVersion(),
                                getCurrentSettingsFromCache().getPlantGrowthList().size()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Bukkit.getLogger().log(Level.FINER, gson.toJson(getCurrentSettingsFromCache()));
    }

    private void loadCurrentSettingsAndCacheFromFile() {
        try {
            cachedCurrentSettings = loadYMLFile(getSettingsPath());
        }
        catch (IOException e) {
            ControlledPlantGrowth.handleException(e);
        }
    }

    private void loadCurrentSettingsAndCacheFromDatabase() {
        cachedCurrentSettings = getSettingPageByName(getConfigSettingsPageName());

        if (cachedCurrentSettings != null) {
            return;
        }

        SettingsDTO settingsDTO = getDefault();
        settingsDTO.setSettingsPageName(getConfigSettingsPageName());
        cachedCurrentSettings = settingsDTO;
        saveCachedCurrentSettings();
    }

    @Override
    public SettingsDTO getCurrentSettingsFromCache() {
        return cachedCurrentSettings;
    }

    @Override
    public void saveCachedCurrentSettings() {
        if (isUsingDB()) {
            Bukkit.getLogger().log(Level.INFO, "Saving current settings to database ...");
            saveCachedCurrentSettingsToDatabase();
        }
        else {
            Bukkit.getLogger().log(Level.INFO, "Saving current settings to file ...");
            saveCachedCurrentSettingsToFile();
        }

        loadCurrentSettingsAndCache();
    }

    private void saveCachedCurrentSettingsToFile() {
        try {
            saveYMLFile(getCurrentSettingsFromCache(), getSettingsPath());
        }
        catch (IOException e) {
            ControlledPlantGrowth.handleException(e);
        }
    }

    private void saveCachedCurrentSettingsToDatabase() {
        try (Session session = DatabaseHibernateSettingsUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(getCurrentSettingsFromCache());

            transaction.commit();
        }
        catch (Exception e) {
            ControlledPlantGrowth.handleException(e);
        }
    }

    @Override
    public File getSettingsPath() {
        File fc = ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getDataFolder();

        return new File(fc, "plantSettings.yml");
    }

    @Override
    public SettingsDTO getDefault() {
        SettingsDTO defaultSettings = new SettingsDTO();

        defaultSettings.setMaximumAmountOfPlantsInATimeWindowCluster(1);
        defaultSettings.setMaximumTimeWindowInMillisecondsForPlantsToBeClustered(1);
        defaultSettings.setSettingsPageName("default");
        defaultSettings.setUseAggressiveChunkAnalysisAndLookForUnregisteredPlants(true);
        defaultSettings.setShowInfoWhenDefaultSettingIsUsed(false);

        ArrayList<SettingsPlantGrowthDTO> settingsPlantGrowths = new ArrayList<>();

        // grow wheat in 10 seconds
        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.WHEAT, false, 1).setArray(new int[]{
                3, 3, 3, 4, 2, 2, 1
        }, TimeUnit.MINUTES));

        settingsPlantGrowths.add(new SettingsPlantGrowthDTO(Material.BEETROOTS, true, 18));

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
}
