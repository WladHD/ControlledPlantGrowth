package de.wladtheninja.controlledplantgrowth.growables.concepts.constraints;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;

import java.text.MessageFormat;

@AllArgsConstructor
public class LightLevelPlantGrowthConstraint implements IPlantGrowthConstraint {

    @Getter
    private final byte minLightLevel;


    @Override
    public boolean isGrowthConditionFulfilled(IPlantConcept pos, Block b) {
        return b.getLightLevel() >= minLightLevel;
    }

    @Override
    public String getGeneralViolationMessage() {
        return MessageFormat.format("Current light level is to low for the plant to grow. Light level needed is {0}",
                minLightLevel);
    }
}
