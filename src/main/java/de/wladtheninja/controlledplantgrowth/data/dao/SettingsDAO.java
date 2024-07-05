package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;

import static lombok.AccessLevel.PRIVATE;


@NoArgsConstructor(access = PRIVATE)
public class SettingsDAO {

    @Getter
    @Setter
    private SettingsDTO currentSettings;

    @Getter(lazy = true)
    private static final SettingsDAO instance = new SettingsDAO();

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

    public void saveSettings(SettingsDTO settings) {
        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(settings);

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
