package de.wladtheninja.controlledplantgrowth.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class ConfigDTO implements Serializable {
    private boolean loadConfigFromDatabase;
    private boolean enableDebugLog;
    private SettingsDTO plantGrowthSettings;
    private Map<String, String> databaseHibernateSettings;
}
