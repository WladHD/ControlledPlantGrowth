package de.wladtheninja.controlledplantgrowth.growables.concepts.err;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.concepts.constraints.IPlantGrowthConstraint;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;

@AllArgsConstructor
public class PlantConstraintViolationException extends Exception {

    private final List<IPlantGrowthConstraint> violations;
    private final Block plant;
    private final IPlantConceptBasic plantConcept;

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(MessageFormat.format(
                "{0} plant growth constraint{1} violated. {2} at {3} can not grow" + ".\nList of violated " +
                        "constraints: ",
                violations.size(),
                violations.size() == 1 ?
                        " was" :
                        "s were",
                plant.getType(),
                plant.getLocation().toVector()));

        sb.append("\n");

        violations.forEach(vio -> sb.append(vio.getGeneralViolationMessage()));

        return sb.toString();
    }

    public void printInformation() {
        Bukkit.getLogger().log(Level.FINER, getMessage());
    }

}
