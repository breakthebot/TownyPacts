package org.breakthebot.townyPacts;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import org.breakthebot.townyPacts.commands.sendPact;
import org.breakthebot.townyPacts.events.PvP;
import org.breakthebot.townyPacts.events.Overclaim;
import org.breakthebot.townyPacts.events.Update;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
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
        event(new sendPact(), this);
        event(new Overclaim(), this);
        event(new PvP(), this);
        event(new Update(), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("TownyPacts has been disabled!");
        instance = null;
    }

    public void event(Listener listener, Plugin plugin) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static TownyPacts getInstance() {
        return instance;
    }

    public config getConfiguration() { return this.configuration; }

}