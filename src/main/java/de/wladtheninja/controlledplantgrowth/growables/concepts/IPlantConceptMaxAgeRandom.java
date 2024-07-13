package de.wladtheninja.controlledplantgrowth.growables.concepts;


import org.bukkit.Location;

import java.util.HashMap;
import java.util.Random;

public interface IPlantConceptMaxAgeRandom {

    int getMaxAgeUpperBound();

    int getMaxAgeLowerBound();

    Random getRandom();

    HashMap<Location, Integer> getRandomMaxAgeMap();

    void removeFromMaxAgeMap(Location location);

    int getMaxAgeFromMap(Location location);
}
