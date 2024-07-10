package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.dto.PlantLocationChunkDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlantLocationChunkDAO implements IPlantLocationChunkDAO<PlantLocationChunkDTO, PlantBaseBlockDTO> {

    @Override
    public List<PlantLocationChunkDTO> getAll() {
        List<PlantLocationChunkDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantLocationChunkDTO";
            Query<PlantLocationChunkDTO> query = session.createQuery(hql, PlantLocationChunkDTO.class);
            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            handleException(ex);
            return null;
        }

        return retrievedBlocks;
    }

    @Override
    public List<PlantBaseBlockDTO> getAllPlantBasesByChunk(World w, int x, int z) {
        List<PlantBaseBlockDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantBaseBlockDTO where plantLocationChunkDTO.x= :x and plantLocationChunkDTO.z= :z " +
                    "and plantLocationChunkDTO.worldUID = :worldUID";
            Query<PlantBaseBlockDTO> query = session.createQuery(hql, PlantBaseBlockDTO.class);
            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            handleException(ex);
            return null;
        }

        return retrievedBlocks;
    }

    @Override
    public List<PlantLocationChunkDTO> merge(List<PlantLocationChunkDTO> objects) {
        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            objects = objects.stream().map(session::merge).collect(Collectors.toList());

            transaction.commit();
        }
        catch (Exception ex) {
            handleException(ex);
            return null;
        }

        return objects;
    }

    @Override
    public PlantLocationChunkDTO merge(PlantLocationChunkDTO objects) {
        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            objects = session.merge(objects);

            transaction.commit();
        }
        catch (Exception ex) {
            handleException(ex);
            return null;
        }

        return objects;
    }

    @Override
    public boolean delete(World w, int x, int z) {
        int deletedCount = 0;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "delete from PlantLocationChunkDTO where x= :x and z= :z " + "and worldUID = :worldUID";

            deletedCount = session.createMutationQuery(hql)
                    .setParameter("x", x)
                    .setParameter("z", z)
                    .setParameter("worldUID", w.getUID())
                    .executeUpdate();

            transaction.commit();

            Bukkit.getLogger().log(Level.FINER, MessageFormat.format("Deleted record for chunk at {0},{1}", x, z));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return deletedCount != 0;
    }

    @Override
    public PlantLocationChunkDTO getByChunk(Chunk l) {
        return getByChunkCoordinates(l.getWorld(), l.getX(), l.getZ());
    }

    @Override
    public PlantLocationChunkDTO getByChunkCoordinates(World w, int x, int z) {
        List<PlantLocationChunkDTO> retrievedBlocks;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM PlantLocationChunkDTO WHERE x = :x AND " + "z = :z AND worldUID = :worldUID";
            Query<PlantLocationChunkDTO> query = session.createQuery(hql, PlantLocationChunkDTO.class);
            query.setParameter("x", x);
            query.setParameter("z", z);
            query.setParameter("worldUID", w.getUID());

            retrievedBlocks = query.getResultList();

            transaction.commit();
        }
        catch (Exception ex) {
            handleException(ex);
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
    public void handleException(Exception ex) {
        Bukkit.getLogger().log(Level.INFO, ex.getMessage(), ex);
    }


}
