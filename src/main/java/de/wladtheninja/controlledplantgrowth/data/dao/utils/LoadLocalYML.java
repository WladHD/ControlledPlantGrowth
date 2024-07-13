package de.wladtheninja.controlledplantgrowth.data.dao.utils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import jakarta.xml.bind.DataBindingException;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;

@AllArgsConstructor
public abstract class LoadLocalYML<T> implements ILoadLocalYML<T> {
    private final Class<T> fileDTO;

    @Override
    public T loadYMLFile(File configFile) throws IOException {
        if (configFile == null) {
            return null;
        }

        if (!configFile.exists()) {
            saveYMLFile(getDefault(), configFile);
        }

        YAMLMapper yamlMapper = new YAMLMapper().configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);

        try {
            return yamlMapper.readValue(configFile, fileDTO);
        }
        catch (DataBindingException | StreamReadException ex) {
            ControlledPlantGrowth.handleException(ex);
        }

        return null;
    }

    @Override
    public abstract T getDefault();

    @Override
    public void saveYMLFile(T currentFile, File configFile) throws IOException {
        if (configFile == null) {
            return;
        }

        YAMLMapper yamlMapper = new YAMLMapper().configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);

        File pluginDirectory = configFile.getParentFile();

        if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
            Bukkit.getLogger()
                    .log(Level.SEVERE,
                            MessageFormat.format("Could not create folder {0} for {1}",
                                    pluginDirectory,
                                    configFile.getName()));
        }

        yamlMapper.writeValue(configFile, currentFile);
    }
}
