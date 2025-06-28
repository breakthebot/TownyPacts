package org.breakthebot.townyPacts;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.breakthebot.townyPacts.commands.pactRegister;

public final class TownyPacts extends JavaPlugin {
    private static TownyPacts instance;

    @Override
    public void onEnable() {
        instance = this;

        AddonCommand pactCommand = new AddonCommand(
                TownyCommandAddonAPI.CommandType.NATION,
                "pact",
                new pactRegister()
        );
        TownyCommandAddonAPI.addSubCommand(pactCommand);

        getLogger().info("TownyPacts has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyPacts has been disabled!");
        instance = null;
    }

    public static TownyPacts getInstance() {
        return instance;
    }
}