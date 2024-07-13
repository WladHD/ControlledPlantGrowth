package de.wladtheninja.controlledplantgrowth.data.utils;

import lombok.AllArgsConstructor;

import java.text.MessageFormat;

@AllArgsConstructor
public enum SettingsDTOVersion {

    SETTINGS_V2("2.0.0");

    public final String versionString;

    public static SettingsDTOVersion getCurrentVersion() {
        return SETTINGS_V2;
    }

    public String toString() {
        return MessageFormat.format("v{0}", versionString);
    }
}
