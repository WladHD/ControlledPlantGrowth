package de.wladtheninja.controlledplantgrowth.data.dto.external;

import de.wladtheninja.controlledplantgrowth.data.utils.SettingsDTOVersion;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class ConfigDTO
        implements Serializable
{
    private boolean notifyOnSpigotRelease;
    private boolean notifyOnGitHubExperimentalRelease;
    private boolean loadPlantSettingsFromDatabase;
    private boolean enableDebugLog;
    private String activeSettingsPage;
    private Map<String, String> hibernateConfigPlantSettings;
    private Map<String, String> hibernateConfigLocalPlantCache;
    private SettingsDTOVersion currentSettingsVersion;
}
