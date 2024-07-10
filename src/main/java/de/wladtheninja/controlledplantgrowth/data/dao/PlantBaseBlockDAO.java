package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlantBaseBlockDAO {

    @Getter(lazy = true)
    private static final PlantBaseBlockDAO instance = new PlantBaseBlockDAO();

    public List<PlantBaseBlockDTO> getPlantBaseBlocksByChunk(Chunk c) {
        List<PlantBaseBlockDTO> retrievedBlocks = null;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE plantBaseBlockChunkDTO.xChunk = :xChunk " +
                    "AND plantBaseBlockChunkDTO.zChunk = :zChunk AND plantBaseBlockIdDTO.worldUID = :worldUID";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("xChunk", c.getX());
            query.setParameter("zChunk", c.getZ());
            query.setParameter("worldUID", c.getWorld().getUID());

            retrievedBlocks = query.getResultList();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return retrievedBlocks;
    }

    public List<PlantBaseBlockDTO> getPlantBaseBlocksByDatabaseType(Material databaseMaterial) {
        List<PlantBaseBlockDTO> retrievedBlocks = null;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE plantType = :plant";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("plant", databaseMaterial);

            retrievedBlocks = query.getResultList();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return retrievedBlocks;
    }

    public PlantBaseBlockDTO getPlantBaseBlockByBlock(Block b) {
        List<PlantBaseBlockDTO> retrievedBlocks = null;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE plantBaseBlockIdDTO.x = :x AND plantBaseBlockIdDTO.y = :y AND " +
                    "plantBaseBlockIdDTO.z = :z AND plantBaseBlockIdDTO.worldUID = :worldUID";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("x", b.getLocation().getBlockX());
            query.setParameter("y", b.getLocation().getBlockY());
            query.setParameter("z", b.getLocation().getBlockZ());

            if (b.getLocation().getWorld() == null) {
                throw new RuntimeException("World in given location is null.");
            }

            query.setParameter("worldUID", b.getLocation().getWorld().getUID());

            retrievedBlocks = query.getResultList();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
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

    public boolean deletePlantBaseBlock(Block b) {
        int deletedCount = 0;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql =
                    "delete from PlantBaseBlockDTO where plantBaseBlockIdDTO.x = :x and plantBaseBlockIdDTO.y = :y " +
                            "and plantBaseBlockIdDTO.z = :z and plantBaseBlockIdDTO.worldUID = :worldUID";

            deletedCount = session.createMutationQuery(hql)
                    .setParameter("x", b.getLocation().getBlockX())
                    .setParameter("y", b.getLocation().getBlockY())
                    .setParameter("z", b.getLocation().getBlockZ())
                    .setParameter("worldUID", Objects.requireNonNull(b.getLocation().getWorld()).getUID())
                    .executeUpdate();

            transaction.commit();

            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("Deleted record for block at {0}", b.getLocation().toVector()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return deletedCount != 0;
    }

    public void updatePlantBaseBlock(PlantBaseBlockDTO updatedDto) {
        if (updatedDto.getPlantBaseBlockIdDTO() == null) {
            throw new RuntimeException("Primary key missing");
        }

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            PlantBaseBlockDTO updated = session.merge(updatedDto);

            transaction.commit();

            Bukkit.getLogger()
                    .log(Level.FINER, MessageFormat.format("Updated record at {0}", updated.getPlantBaseBlockIdDTO()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public List<PlantBaseBlockDTO> getPastUpdates(long timeEpoch) {
        List<PlantBaseBlockDTO> pbb;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE timeNextGrowthStage != -1 AND timeNextGrowthStage <= " +
                    ":timeEpoch" + " ORDER BY timeNextGrowthStage ASC";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("timeEpoch", timeEpoch); // IGNORE TIME EPOCH FOR NOW; HANDLE OLD PLANTS TOO

            pbb = query.getResultList();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return pbb;
    }

    public List<PlantBaseBlockDTO> getNextFutureUpdate(long timeBoundaryLow,
                                                       long clusterWindowMilliseconds) {
        List<PlantBaseBlockDTO> pbb;

        clusterWindowMilliseconds = Math.max(1, clusterWindowMilliseconds);

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO WHERE timeNextGrowthStage != -1 " +
                    "AND timeNextGrowthStage > :timeBoundaryLow ORDER BY timeNextGrowthStage ASC";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            query.setParameter("timeBoundaryLow", timeBoundaryLow);
            query.setMaxResults(Math.max(1,
                                         SettingsDAO.getInstance()
                                                 .getCurrentSettings()
                                                 .getMaximumAmountOfPlantsInATimeWindowCluster()));
            pbb = query.getResultList();


            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        if (!pbb.isEmpty()) {
            PlantBaseBlockDTO first = pbb.getFirst();

            final long finalClusterWindowMilliseconds = clusterWindowMilliseconds;
            return pbb.stream()
                    .filter(others -> Math.abs(others.getTimeNextGrowthStage() - first.getTimeNextGrowthStage()) <
                            finalClusterWindowMilliseconds)
                    .collect(Collectors.toList());
        }

        return pbb;
    }

    public void persistNewPlantBaseBlock(PlantBaseBlockDTO pbb) {

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.merge(pbb);

            transaction.commit();

            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("Registered {0} at {1} with a current plant stage of {2}",
                                              pbb.getLocation().getBlock().getType(),
                                              pbb.getLocation().getBlock().getLocation().toVector(),
                                              pbb.getCurrentPlantStage()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
