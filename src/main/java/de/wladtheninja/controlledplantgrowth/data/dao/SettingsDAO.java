package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dto.ConfigDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.embedded.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.setup.SetupConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;


@Setter
@Getter
public class SettingsDAO implements ISettingsDAO<SettingsDTO, SettingsPlantGrowthDTO, ConfigDTO> {

    private SettingsDTO currentSettings;

    private ConfigDTO currentConfig;

    public List<SettingsDTO> getAllActiveSettings() {
        List<SettingsDTO> activeSettings = null;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM SettingsDTO WHERE active = true";
            Query<SettingsDTO> query = session.createQuery(hql, SettingsDTO.class);

            activeSettings = query.getResultList();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return activeSettings;
    }

    public SettingsPlantGrowthDTO getPlantSettings(Material m) {
        final SettingsPlantGrowthDTO s = getCurrentSettings().getPlantGrowthList()
                .stream()
                .filter(entry -> entry.getMaterial() == m)
                .findFirst()
                .orElse(null);


        if (s != null) {
            return s;
        }

        if (getCurrentSettings().isShowInfoWhenDefaultSettingIsUsed()) {
            Bukkit.getLogger()
                    .log(Level.INFO,
                         MessageFormat.format("Plant {0} had no rules for growing defined. Using the default rules " +
                                                      "instead. If this behaviour is wanted, ignore this message or " +
                                                      "turn the setting showInfoWhenDefaultSettingIsUsed to false.",
                                              m));
        }

        return getCurrentSettings().getPlantGrowthList()
                .stream()
                .filter(entry -> entry.getMaterial() == Material.AIR)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Settings may be corrupted. Could not locate the default settings."));
    }

    public void deleteAllActiveSettings() {
        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "DELETE FROM SettingsDTO WHERE active = true";
            session.createMutationQuery(hql).executeUpdate();

            transaction.commit();
        } catch (Exception exception) {

        }

    }

    public void saveSettings() {
        saveSettings(getCurrentSettings());
    }

    public void saveSettings(SettingsDTO settings) {
        if (!getCurrentConfig().isLoadConfigFromDatabase()) {
            getCurrentConfig().setPlantGrowthSettings(settings);
            try {
                SetupConfig.saveConfig(getCurrentConfig(),
                                       ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class).getDataFolder());
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
                return;
            }

            return;
        }

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(settings);

            transaction.commit();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
