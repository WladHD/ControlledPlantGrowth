package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import de.wladtheninja.controlledplantgrowth.data.PlantDataManager;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateLocalPlantCacheUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlantBaseBlockDAO implements IPlantBaseBlockDAO<PlantBaseBlockDTO> {

    @Override
    public List<PlantBaseBlockDTO> getAll() {
        List<PlantBaseBlockDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return retrievedBlocks;
    }

    @Override
    public PlantBaseBlockDTO merge(PlantBaseBlockDTO obj) {
        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("[DB] Saving {0} at {1} (DB Type {2})",
                                obj.getLocation().getBlock().getType(),
                                obj.getLocation().toVector(),
                                obj.getPlantType()));
        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            obj = session.merge(obj);

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return obj;
    }

    @Override
    public List<PlantBaseBlockDTO> merge(List<PlantBaseBlockDTO> objects) {
        List<PlantBaseBlockDTO> list = new ArrayList<>();

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();


            objects.forEach(obj -> list.add(session.merge(obj)));

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return list;
    }

    @Override
    public boolean delete(Location l) {
        Bukkit.getLogger()
                .log(Level.FINER,
                        MessageFormat.format("[DB] Deleting {0} at {1}", l.getBlock().getType(), l.toVector()));
        int deletedCount = 0;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql =
                    "delete from PlantBaseBlockDTO where plantBaseBlockIdDTO.x = :x and plantBaseBlockIdDTO.y = :y " +
                            "and plantBaseBlockIdDTO.z = :z and plantBaseBlockIdDTO.worldUID = :worldUID";

            deletedCount = session.createMutationQuery(hql)
                    .setParameter("x", l.getBlockX())
                    .setParameter("y", l.getBlockY())
                    .setParameter("z", l.getBlockZ())
                    .setParameter("worldUID", Objects.requireNonNull(l.getWorld()).getUID())
                    .executeUpdate();

            transaction.commit();
        }
        catch (Exception exception) {
            ControlledPlantGrowth.handleException(exception);
        }

        return deletedCount != 0;
    }

    @Override
    public PlantBaseBlockDTO getByLocation(Location location) {
        List<PlantBaseBlockDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE plantBaseBlockIdDTO.x = :x AND plantBaseBlockIdDTO.y = :y AND " +
                    "plantBaseBlockIdDTO.z = :z AND plantBaseBlockIdDTO.worldUID = :worldUID";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("x", location.getBlockX());
            query.setParameter("y", location.getBlockY());
            query.setParameter("z", location.getBlockZ());

            if (location.getWorld() == null) {
                throw new RuntimeException("World in given location is null.");
            }

            query.setParameter("worldUID", location.getWorld().getUID());

            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        if (retrievedBlocks == null) {
            return null;
        }

        if (retrievedBlocks.size() > 1) {
            throw new RuntimeException(
                    "This is illegal since the cords are treated as unique primary key ... but yet here we are.");
        }

        return retrievedBlocks.isEmpty() ?
                null :
                retrievedBlocks.getFirst();
    }

    @Override
    public List<PlantBaseBlockDTO> getByMaterial(Material material) {
        List<PlantBaseBlockDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE plantType = :plant";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("plant", material);

            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return retrievedBlocks;
    }

    @Override
    public List<PlantBaseBlockDTO> getByChunk(Chunk c) {
        List<PlantBaseBlockDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE chunkX = :xChunk " +
                    "AND chunkZ = :zChunk AND plantBaseBlockIdDTO.worldUID = :worldUID";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("xChunk", c.getX());
            query.setParameter("zChunk", c.getZ());
            query.setParameter("worldUID", c.getWorld().getUID());

            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return retrievedBlocks;
    }

    @Override
    public List<PlantBaseBlockDTO> getAfterTimestamp(long timeStamp, int groupMilliseconds, int limit) {
        List<PlantBaseBlockDTO> pbb;

        groupMilliseconds = Math.max(1, groupMilliseconds);

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE timeNextGrowthStage != :growStage AND timeNextGrowthStage > " +
                    ":timeBoundaryLow ORDER BY " + "timeNextGrowthStage ASC";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("timeBoundaryLow", timeStamp);
            query.setParameter("growStage", -1);
            query.setMaxResults(Math.max(1,
                    PlantDataManager.getInstance()
                            .getSettingsDataBase()
                            .getCurrentSettingsFromCache()
                            .getMaximumAmountOfPlantsInATimeWindowCluster()));
            pbb = query.getResultList();


            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        if (!pbb.isEmpty()) {
            PlantBaseBlockDTO first = pbb.getFirst();

            final long finalClusterWindowMilliseconds = groupMilliseconds;
            return pbb.stream()
                    .filter(others -> Math.abs(others.getTimeNextGrowthStage() - first.getTimeNextGrowthStage()) <
                            finalClusterWindowMilliseconds)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return pbb;
    }

    @Override
    public List<PlantBaseBlockDTO> getBeforeTimestamp(long timeStamp) {
        List<PlantBaseBlockDTO> pbb;

        try (Session session = DatabaseHibernateLocalPlantCacheUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE timeNextGrowthStage != :growStage AND timeNextGrowthStage <= " +
                    ":timeEpoch ORDER BY timeNextGrowthStage ASC";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("timeEpoch", timeStamp); // IGNORE TIME EPOCH FOR NOW; HANDLE OLD PLANTS TOO
            query.setParameter("growStage", -1);

            pbb = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            ControlledPlantGrowth.handleException(ex);
            return null;
        }

        return pbb;
    }

}
