package de.wladtheninja.controlledplantgrowth.data.utils;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.dao.SettingsDAO;
import de.wladtheninja.controlledplantgrowth.data.dto.ConfigDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseHibernateUtil {

    @Getter(lazy = true)
    private static final DatabaseHibernateUtil instance = new DatabaseHibernateUtil();
    private SessionFactory sessionFactory;

    public void setup() {

        Bukkit.getLogger().log(Level.FINER, "Setting up database 1");

        if (sessionFactory != null) {
            return;
        }

        try {
            Bukkit.getLogger().log(Level.FINER, "Setting up database 2");

            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(SettingsDTO.class);
            configuration.addAnnotatedClass(PlantBaseBlockDTO.class);

            ConfigDTO pluginConfig = SettingsDAO.getInstance().getCurrentConfig();

            pluginConfig.getDatabaseHibernateSettings().forEach(configuration::setProperty);

            Bukkit.getLogger().log(Level.FINER, "Setting up database 4");

            BootstrapServiceRegistry brs =
                    new BootstrapServiceRegistryBuilder().applyClassLoader(ControlledPlantGrowth.class.getClassLoader())
                            .enableAutoClose()
                            .build();

            StandardServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder(brs).applySettings(configuration.getProperties()).build();
            Bukkit.getLogger().log(Level.FINER, "Setting up database 5");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            Bukkit.getLogger().log(Level.FINER, "Setting up database 6");
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.FINER, "Setting up database 7");
            Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        }
    }

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            setup();
        }

        return sessionFactory;
    }

    public void shutdown() {
        getSessionFactory().close();
    }
}