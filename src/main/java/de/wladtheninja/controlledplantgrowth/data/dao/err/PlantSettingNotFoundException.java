package de.wladtheninja.controlledplantgrowth.data.dao.err;

import lombok.Getter;
import org.bukkit.Material;

import java.text.MessageFormat;

@Getter
public class PlantSettingNotFoundException extends Exception {

    private final Material searchMaterial;

    public PlantSettingNotFoundException(Material search) {

        super(MessageFormat.format("Setting was not found for {0}", search));

        this.searchMaterial = search;
    }
}
