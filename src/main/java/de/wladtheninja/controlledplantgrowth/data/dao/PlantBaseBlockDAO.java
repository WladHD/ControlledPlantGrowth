package de.wladtheninja.controlledplantgrowth.data.dao;

import de.wladtheninja.controlledplantgrowth.data.dto.PlantBaseBlockDTO;
import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConcept;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptAge;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptGrowthInformation;
import de.wladtheninja.controlledplantgrowth.growables.concepts.IPlantConceptLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlantBaseBlockDAO {

    @Getter(lazy = true)
    private static final PlantBaseBlockDAO instance = new PlantBaseBlockDAO();

    public int unregisterPlantBase(Block b) {
        int deletedCount = 0;

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "delete from PlantBaseBlockDTO where x = :x and y = :y and z = :z and worldUID = :worldUID";

            deletedCount = session.createMutationQuery(hql)
                    .setParameter("x", b.getLocation().getBlockX())
                    .setParameter("y", b.getLocation().getBlockY())
                    .setParameter("z", b.getLocation().getBlockZ())
                    .setParameter("worldUID", Objects.requireNonNull(b.getLocation().getWorld()).getUID())
                    .executeUpdate();

            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return deletedCount;
    }

    public void registerPlantBase(IPlantConcept ipc,
                                  Block b) {
        if (!(ipc instanceof IPlantConceptLocation)) {
            throw new RuntimeException("Plant did not provide location information.");
        }

        final Block plantBlock = ((IPlantConceptLocation) ipc).getPlantRootBlock(b);

        PlantBaseBlockDTO pbb = new PlantBaseBlockDTO(plantBlock.getLocation());

        if (ipc instanceof IPlantConceptAge) {
            pbb.setCurrentPlantStage(((IPlantConceptAge) ipc).getCurrentAge(plantBlock));
        }
        else if (ipc instanceof IPlantConceptGrowthInformation) {
            pbb.setCurrentPlantStage(((IPlantConceptGrowthInformation) ipc).isMature(plantBlock) ?
                                             1 :
                                             0);
        }

        try (Session session = DatabaseHibernateUtil.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(pbb);

            transaction.commit();

            Bukkit.getLogger()
                    .log(Level.FINER,
                         MessageFormat.format("Registered {0} at {1} with a current plant stage of {2}",
                                              plantBlock.getType(),
                                              plantBlock.getLocation().toVector(),
                                              pbb.getCurrentPlantStage()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
