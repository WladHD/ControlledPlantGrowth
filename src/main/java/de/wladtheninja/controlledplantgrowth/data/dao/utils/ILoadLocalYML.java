package de.wladtheninja.controlledplantgrowth.data.dao.utils;

import java.io.File;
import java.io.IOException;

public interface ILoadLocalYML<T> {

    T loadYMLFile(File configFile) throws IOException;

    T getDefault();

    void saveYMLFile(T currentFile, File configFile) throws IOException;

}
