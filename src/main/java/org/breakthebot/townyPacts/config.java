package org.breakthebot.townyPacts;

import org.bukkit.configuration.file.FileConfiguration;

public class config {


    public final int breakCooldownDays;

    public config(TownyPacts plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.breakCooldownDays = cfg.getInt("break-cooldown-days", 3);
    }
}
