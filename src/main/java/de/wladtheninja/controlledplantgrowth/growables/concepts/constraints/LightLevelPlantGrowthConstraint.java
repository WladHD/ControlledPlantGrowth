package de.wladtheninja.controlledplantgrowth.growables.concepts.constraints;

import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public class LightLevelPlantGrowthConstraint implements IPlantGrowthConstraint {

    private final byte minLightLevel;


    @Override
    public boolean isGrowthConditionFulfilled(IPlantConceptBasic pos, Block b) {
        return b.getLightLevel() >= minLightLevel;
    }

    @Override
    public String getGeneralViolationMessage() {
        return MessageFormat.format("Current light level is to low for the plant to grow. Minimal light level: {0}",
                minLightLevel);
    }
}
