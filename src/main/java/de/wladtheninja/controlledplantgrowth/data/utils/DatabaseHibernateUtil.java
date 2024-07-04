package de.wladtheninja.controlledplantgrowth.data.utils;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.SettingsDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseHibernateUtil {

    @Getter(lazy = true)
    private static final DatabaseHibernateUtil instance = new DatabaseHibernateUtil();
    private SessionFactory sessionFactory;

    public void setup() {
        if (sessionFactory != null) {
            return;
        }

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(PlantBaseBlockDTO.class);
        configuration.addAnnotatedClass(SettingsDTO.class);

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

        StandardServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
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