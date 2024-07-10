package de.wladtheninja.controlledplantgrowth.growables.growthlogic;

import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantLocationChunkDTO;
import de.wladtheninja.controlledplantgrowth.growables.ControlledPlantGrowthManager;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptBasic;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantNoAgeableInterfaceException;
import de.wladtheninja.controlledplantgrowth.growables.concepts.err.PlantRootBlockMissingException;
import de.wladtheninja.controlledplantgrowth.growables.growthlogic.utils.PlantDataUtils;
import de.wladtheninja.controlledplantgrowth.utils.DebounceUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlantInternEventListener implements IPlantInternEventListener {

    @Getter
    private final DebounceUtil debounceUtil = new DebounceUtil();

    @Override
    public void onPlantStructureUpdateEvent(IPlantConceptBasic ipc, Location definitePlantRootLocation) {
        if (!definitePlantRootLocation.getChunk().isLoaded()) {
            onChunkUnloadEvent(definitePlantRootLocation.getChunk());
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
            PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            return;
        }

        final boolean isMature = ipc.isMature(definitePlantRootLocation.getBlock());

        if (isMature && isSaved) {
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
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
        }

        if (newAgeAndTimestamp == null) {
            if (isSaved) {
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }
            return;
        }


        if (newAgeAndTimestamp.getKey() == -1) {
            if (isSaved) {
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }

            ipc.setToFullyMature(definitePlantRootLocation.getBlock());
            return;
        }

        plantDto.setCurrentPlantStage(Objects.requireNonNull(newAgeAndTimestamp).getKey());
        plantDto.setTimeNextGrowthStage(Objects.requireNonNull(newAgeAndTimestamp).getValue());

        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("Next update {3} in {0}ms, current growth {1}",
                                plantDto.getTimeNextGrowthStage() - System.currentTimeMillis(),
                                plantDto.getCurrentPlantStage(), plantDto.getLocation().getBlock().getType()));


        if (!(ipc instanceof IPlantConceptAge)) {
            PlantDataManager.getInstance().getPlantDataBase().merge(plantDto);
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
            return;
        }

        try {
            ((IPlantConceptAge) ipc).setCurrentAge(definitePlantRootLocation.getBlock(),
                    plantDto.getCurrentPlantStage());

            PlantDataManager.getInstance().getPlantDataBase().merge(plantDto);
            ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
        }
        catch (PlantNoAgeableInterfaceException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
            if (isSaved) {
                PlantDataManager.getInstance().getPlantDataBase().delete(definitePlantRootLocation);
            }
        }

    }

    @Override
    public void onChunkLoadEvent(Chunk c) {
        onChunkEvent(c, true);
    }

    public void onChunkEvent(Chunk c, boolean load) {
        PlantLocationChunkDTO chunkDTO = PlantDataManager.getInstance().getPlantChunkDataBase().getByChunk(c);

        if (chunkDTO == null) {
            return;
        }

        chunkDTO.setLoaded(load);
        PlantDataManager.getInstance().getPlantChunkDataBase().merge(chunkDTO);

        List<PlantBaseBlockDTO> plantsInChunk = PlantDataManager.getInstance()
                .getPlantChunkDataBase()
                .getAllPlantBasesByChunk(c.getWorld(), c.getX(), c.getZ());

        if (plantsInChunk == null || plantsInChunk.isEmpty()) {
            PlantDataManager.getInstance().getPlantChunkDataBase().delete(c.getWorld(), c.getX(), c.getZ());
            return;
        }

        ControlledPlantGrowthManager.getInstance().getClockwork().startPlantUpdateQueue();
    }

    @Override
    public void onChunkUnloadEvent(Chunk c) {
        onChunkEvent(c, false);
    }

    @Override
    public void onPossiblePlantStructureModifyEvent(Material possiblePlantMaterial, Location possibleRoot) {
        if (!possibleRoot.getChunk().isLoaded()) {
            onChunkUnloadEvent(possibleRoot.getChunk());
            return;
        }

        IPlantConceptBasic ipc = ControlledPlantGrowthManager.getInstance()
                .retrieveSuitedPlantConcept(possiblePlantMaterial);

        if (ipc == null) {
            return;
        }

        try {
            final Block definiteRootBlock = ipc.getPlantRootBlock(possibleRoot.getBlock());

            debounceUtil.debounce(definiteRootBlock.getLocation(),
                    () -> onPlantStructureUpdateEvent(ipc, definiteRootBlock.getLocation()),
                    150,
                    TimeUnit.MILLISECONDS);
        }
        catch (PlantRootBlockMissingException e) {
            Bukkit.getLogger().log(Level.FINER, e.getMessage(), e);
            PlantDataManager.getInstance().getPlantDataBase().delete(possibleRoot);
            return;
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

}
