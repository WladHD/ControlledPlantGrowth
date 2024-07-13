package de.wladtheninja.controlledplantgrowth.utils;

import com.google.gson.Gson;
import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.function.Consumer;

@AllArgsConstructor
// Inspiration taken from https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class SpigotVersionRetriever {

    private final JavaPlugin plugin;
    private final int resourceId;

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URL(MessageFormat.format(
                    "https://api.spiget.org/v2/resources/{0,number,#}/versions/latest",
                    this.resourceId)).openStream(); Scanner sc = new Scanner(is)) {

                StringBuilder sb = new StringBuilder();

                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }

                SpigetVersionDTO spigetVersionDTO = new Gson().fromJson(sb.toString(), SpigetVersionDTO.class);

                consumer.accept(spigetVersionDTO.getName());
            }
            catch (IOException e) {
                plugin.getLogger()
                        .info(MessageFormat.format("Unable to check for updates on SpigotMC: {0}", e.getMessage()));
                ControlledPlantGrowth.handleException(e);
            }
        });
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static final class SpigetVersionDTO {
        private String uuid;
        private String name;
        private long releaseDate;
        private int downloads;
        private Rating rating;

        @AllArgsConstructor
        @Getter
        @Setter
        public static final class Rating {
            private int count;
            private double average;
        }


    }
}
