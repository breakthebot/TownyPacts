package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class LeaderJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!(player.hasPermission("towny.command.nation.pact.manage") || player.hasPermission("towny.command.nation.pact.accept"))) {
            return;
        }

        // Delay by 1 second (20 ticks)
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            Resident res = TownyAPI.getInstance().getResident(player);
            if (res == null || !res.hasNation()) return;

            Nation nation = res.getNationOrNull();
            if (nation == null) return;

            UUID uuid = nation.getUUID();
            if (!leaderMessageQueue.containsKey(uuid)) return;

            String message = leaderMessageQueue.remove(uuid);
            if (message != null) {
                TownyMessaging.sendMsg(player, message);
            }
        }, 20L);
    }
}
