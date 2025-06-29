package org.breakthebot.townyPacts;

import org.bukkit.configuration.file.FileConfiguration;

public class config {


    public final int breakCooldownHours;

    public config(TownyPacts plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.breakCooldownHours = cfg.getInt("break-cooldown-hours", 6);
    }
}
