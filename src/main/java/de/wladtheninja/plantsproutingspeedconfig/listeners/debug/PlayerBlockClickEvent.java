package de.wladtheninja.plantsproutingspeedconfig.listeners.debug;

import de.wladtheninja.plantsproutingspeedconfig.data.HibernateUtil;
import de.wladtheninja.plantsproutingspeedconfig.data.PlantBaseBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.MessageFormat;
import java.util.logging.Level;

public class PlayerBlockClickEvent implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockClickEvent(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getClickedBlock() == null) {
            return;
        }

        Bukkit.getLogger()
                .log(Level.FINER, MessageFormat.format(
                        "Player {0} has interacted with the {1} block on {2}. \nMore information below:",
                        event.getPlayer().getName(), event.getClickedBlock().getType(),
                        event.getClickedBlock().getLocation().toVector()));

        Bukkit.getLogger()
                .log(Level.FINER, MessageFormat.format("Ageable? {0}, Age: {1}",
                        event.getClickedBlock().getBlockData() instanceof Ageable,
                        event.getClickedBlock().getBlockData() instanceof Ageable ?
                                ((Ageable) event.getClickedBlock().getBlockData()).getAge() :
                                -1));

        if (event.getClickedBlock().getBlockData() instanceof Ageable) {
            Bukkit.getLogger().log(Level.FINER, "Trying to increase age ...");
            Ageable ag = (Ageable) event.getClickedBlock().getBlockData();

            ag.setAge(Math.min(ag.getAge() + 1, ag.getMaximumAge()));
            event.getClickedBlock().setBlockData(ag);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            PlantBaseBlock ye = new PlantBaseBlock(event.getClickedBlock().getLocation());
            session.persist(ye);
            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
