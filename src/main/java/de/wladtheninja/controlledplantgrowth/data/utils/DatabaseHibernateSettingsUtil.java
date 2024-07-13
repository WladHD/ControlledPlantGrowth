package de.wladtheninja.controlledplantgrowth.data.utils;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.external.ConfigDTO;
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

import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseHibernateSettingsUtil {

    @Getter(lazy = true)
    private static final DatabaseHibernateSettingsUtil instance = new DatabaseHibernateSettingsUtil();
    private SessionFactory sessionFactory;

    public void setup() {

        getLogger("org.hibernate").setLevel(Level.SEVERE);

        if (sessionFactory != null) {
            return;
        }

        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(SettingsDTO.class);

            ConfigDTO pluginConfig = PlantDataManager.getInstance().getConfigDataBase().getCurrentConfigFromCache();

            pluginConfig.getHibernateConfigPlantSettings().forEach(configuration::setProperty);

            BootstrapServiceRegistry brs =
                    new BootstrapServiceRegistryBuilder().applyClassLoader(ControlledPlantGrowth.class.getClassLoader())
                            .enableAutoClose()
                            .build();

            StandardServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder(brs).applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        catch (Throwable t) {
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