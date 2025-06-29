package org.breakthebot.townyPacts;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import org.breakthebot.townyPacts.commands.sendPact;
import org.bukkit.plugin.java.JavaPlugin;
import org.breakthebot.townyPacts.commands.pactCommand;

public final class TownyPacts extends JavaPlugin {
    private static TownyPacts instance;
    private config configuration;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configuration = new config(this);

        AddonCommand pactCommand = new AddonCommand(
                TownyCommandAddonAPI.CommandType.NATION,
                "pact",
                new pactCommand()
        );
        TownyCommandAddonAPI.addSubCommand(pactCommand);

        getLogger().info("TownyPacts has been enabled!");
        getServer().getPluginManager().registerEvents(new sendPact(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyPacts has been disabled!");
        instance = null;
    }

    public static TownyPacts getInstance() {
        return instance;
    }

    public config getConfiguration() { return this.configuration; }

}