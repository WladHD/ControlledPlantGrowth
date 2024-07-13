package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantRootBlockMissingException;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.utils.PlantDataUtils;
import de.wladtheninja.controlledplantgrowth.growables.instances.PlantInstanceBamboo;
import de.wladtheninja.controlledplantgrowth.utils.DebounceUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Getter
public class PlantInternEventListener implements IPlantInternEventListener {

    private final DebounceUtil debounceUtil = new DebounceUtil();

    private void onPlantStructureUpdateEventDebounced(IPlantConceptBasic ipc, Location definitePlantRootLocation) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getLogger()
                    .log(Level.FINER, "Update request not from main thread... " + "forcing main thread execution.");
            Bukkit.getScheduler()
                    .runTask(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                            () -> onPlantStructureUpdateEvent(ipc, definitePlantRootLocation));
            return;
        }

        if (ipc instanceof PlantInstanceBamboo) {
            Bukkit.getLogger().finer("BAMBOO");
        }

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Update request from main thread... checking {0} at {1}",
                                definitePlantRootLocation.getBlock().getType(),
                                definitePlantRootLocation.toVector()));

        if (!definitePlantRootLocation.getChunk().isLoaded()) {
            Bukkit.getLogger().log(Level.FINER, "Plant requested update, but Chunk is unloaded. Skipping...");
            return;
        }

        PlantBaseBlockDTO plantDto = PlantDataManager.getInstance()
                .getPlantDataBase()
                .getByLocation(definitePlantRootLocation);

        final boolean isGone = !ipc.containsAcceptedMaterial(definitePlantRootLocation.getBlock().getType());
        final boolean isSaved = plantDto != null;

        if (isGone && !isSaved) {
            return;
        }

        if (isGone) {
            ipc.onPlantRemoved(definitePlantRootLocation);
            PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            return;
        }

        final boolean isMature = ipc.isMature(definitePlantRootLocation.getBlock());

        if (isMature && isSaved) {
            ipc.onPlantRemoved(definitePlantRootLocation);
            PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
        }

        if (isMature) {
            return;
        }

        if (plantDto == null) {
            plantDto = new PlantBaseBlockDTO(ipc, definitePlantRootLocation.getBlock());
        }

        Map.Entry<Integer, Long> newAgeAndTimestamp = null;

        try {
            newAgeAndTimestamp = PlantDataUtils.calculateAgeAndNextUpdate(System.currentTimeMillis(), ipc, plantDto);
        }
        catch (PlantNoAgeableInterfaceException e) {
            Bukkit.getLogger().log(Level.FINER, "PlantNoAgeableInterfaceException ...");
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
        }

        if (newAgeAndTimestamp == null) {
            if (isSaved) {
                ipc.onPlantRemoved(definitePlantRootLocation);
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }
            return;
        }

        if (newAgeAndTimestamp.getKey() == -1) {
            if (isSaved) {
                ipc.onPlantRemoved(definitePlantRootLocation);
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }

            ipc.setToFullyMature(definitePlantRootLocation.getBlock());
            return;
        }

        plantDto.setCurrentPlantStage(Objects.requireNonNull(newAgeAndTimestamp).getKey());
        plantDto.setTimeNextGrowthStage(Objects.requireNonNull(newAgeAndTimestamp).getValue());

        long nextUpdateMs = plantDto.getTimeNextGrowthStage() - System.currentTimeMillis();

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format(
                                "{2} at {5} will reach next growth step in {0}ms (~{3}s, ~{4}m), current age {1}",
                                nextUpdateMs,
                                plantDto.getCurrentPlantStage(),
                                plantDto.getLocation().getBlock().getType(),
                                TimeUnit.SECONDS.convert(Math.round(nextUpdateMs), TimeUnit.MILLISECONDS),
                                TimeUnit.MINUTES.convert(Math.round(nextUpdateMs), TimeUnit.MILLISECONDS),
                                plantDto.getLocation().toVector()));


        if (!(ipc instanceof IPlantConceptAge)) {
            PlantDataManager.getInstance().getPlantDataBase().merge(plantDto);
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
            return;
        }

        try {
            Bukkit.getLogger()
                    .log(Level.FINER,
                            MessageFormat.format("Setting current age of {0} to {1} from {2}",
                                    plantDto.getPlantType(),
                                    plantDto.getCurrentPlantStage(),
                                    ((IPlantConceptAge) ipc).getCurrentAge(definitePlantRootLocation.getBlock())));
            ((IPlantConceptAge) ipc).setCurrentAge(definitePlantRootLocation.getBlock(),
                    plantDto.getCurrentPlantStage());

            if (ipc.isMature(definitePlantRootLocation.getBlock())) {
                Bukkit.getLogger()
                        .log(Level.FINER,
                                MessageFormat.format(
                                        "{0} matured, but {1} didnt reflect that... " + "edge case handled.",
                                        definitePlantRootLocation.getBlock().getType(),
                                        "newAgeAndTimestamp"));
                if (isSaved) {
                    ipc.onPlantRemoved(definitePlantRootLocation);
                    PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
                }
                return;
            }

            PlantDataManager.getInstance().getPlantDataBase().merge(plantDto);
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
        }
        catch (PlantNoAgeableInterfaceException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
            if (isSaved) {
                ipc.onPlantRemoved(definitePlantRootLocation);
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }
        }
    }

    @Override
    public void onPlantStructureUpdateEvent(IPlantConceptBasic ipc, Location definitePlantRootLocation) {
        onPlantStructureUpdateEventDebounced(ipc, definitePlantRootLocation);
    }

    @Override
    public void onPossiblePlantStructureModifyEvent(Material possiblePlantMaterial, Location possibleRoot) {
        if (!possibleRoot.getChunk().isLoaded()) {
            return;
        }

        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance()
                .retrieveSuitedPlantConcept(possiblePlantMaterial);

        if (ipc == null) {
            return;
        }

        try {
            Location definiteRootBlock = ipc.getPlantRootBlock(possibleRoot.getBlock()).getLocation();


            Bukkit.getScheduler()
                    .runTaskLater(ControlledPlantGrowth.getPlugin(ControlledPlantGrowth.class),
                            () -> onPlantStructureUpdateEvent(ipc, definiteRootBlock),
                            1);
        }
        catch (PlantRootBlockMissingException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
            ipc.onPlantRemoved(possibleRoot);
            PlantDataManager.getInstance().getPlantDataBase().delete(possibleRoot);
        }
    }

    @Override
    public void onForcePlantsReloadByDatabaseTypeEvent(Material mat) {
        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance().retrieveSuitedPlantConcept(mat);

        if (ipc == null) {
            return;
        }

        PlantDataManager.getInstance().getPlantDataBase().getByMaterial(mat).forEach(pl -> {
            pl.setCurrentPlantStage(-1);
            pl.setTimeNextGrowthStage(-1);
            PlantDataManager.getInstance().getPlantDataBase().merge(pl);
            onPlantStructureUpdateEvent(ipc, pl.getLocation());
        });
    }

    public enum PlantModifyCause {
        PLAYER,
        NATURAL,
        ENTITY
    }

}
