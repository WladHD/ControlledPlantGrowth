package de.wladtheninja.controlledplantgrowth.data.utils;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockIdDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsPlantGrowthDTO;
import de.wladtheninja.controlledplantgrowth.growables.PlantConceptManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.text.MessageFormat;
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
            Bukkit.getLogger().log(Level.FINER, new java.io.File(SettingsDTO.class.getProtectionDomain()
                                                                         .getCodeSource()
                                                                         .getLocation()
                                                                         .getPath())
                    .getName());

            Bukkit.getLogger().log(Level.FINER,
                                   Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            File f = new File("./plugins/" + new java.io.File(PlantConceptManager.class.getProtectionDomain()
                                                                    .getCodeSource()
                                                                    .getLocation()
                                                                    .getPath())
                    .getName());


            Bukkit.getLogger().log(Level.FINER, f.getAbsolutePath());
            configuration.addJar(f);
            configuration.addAnnotatedClass(SettingsDTO.class);
            configuration.addAnnotatedClass(PlantBaseBlockDTO.class);

            /*configuration.addResource("hibernate.cfg.xml");
            configuration.addPackage("de.wladtheninja.controlledplantgrowth.data.dto");
            configuration.addAnnotatedClass(SettingsDTO.class);
            configuration.addAnnotatedClass(SettingsPlantGrowthDTO.class);

            Bukkit.getLogger().log(Level.FINER, "Setting up database 3");


            */

            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            configuration.setProperty("hibernate.connection.url",
                                      MessageFormat.format(
                                              "jdbc:h2:./plugins/ControlledPlantGrowth/data/db;AUTO_SERVER=TRUE",
                                              ""));
            configuration.setProperty("hibernate.connection.username", "sa");
            configuration.setProperty("hibernate.connection.password", "");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");


            Bukkit.getLogger().log(Level.FINER, "Setting up database 4");

            StandardServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            Bukkit.getLogger().log(Level.FINER, "Setting up database 5");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            Bukkit.getLogger().log(Level.FINER, "Setting up database 6");
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.FINER, "Setting up database 7");
            t.printStackTrace();
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