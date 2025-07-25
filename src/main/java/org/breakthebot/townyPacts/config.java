package org.breakthebot.townyPacts;

import org.bukkit.configuration.file.FileConfiguration;

public class config {
    
    public final int baseCreationPrice;
    public final int breakPrice;
    public final int breakCooldownDays;
    public final boolean pactRequireAlly;

    public config(TownyPacts plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.baseCreationPrice = cfg.getInt("baseCreationPrice", 0);
        this.breakPrice = cfg.getInt("breakPrice", 0);
        this.breakCooldownDays = cfg.getInt("breakCooldownDays", 3);
        this.pactRequireAlly = cfg.getBoolean("pactRequireAlly", true);
    }
}
