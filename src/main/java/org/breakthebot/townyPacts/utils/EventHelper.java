package org.breakthebot.townyPacts.utils;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class EventHelper {
    public static void addLeaderMessage(Nation targetNation, String message) {
        ArrayList<Resident> authorised = new ArrayList<>();
        List<Resident> reslist = targetNation.getResidents();
        for (Resident res : reslist) {
            Player tempplayer = res.getPlayer();
            if (tempplayer != null && tempplayer.isOnline()
                    && (tempplayer.hasPermission("towny.command.nation.pact.manage")
                    || tempplayer.hasPermission("towny.command.nation.pact.accept")
                    || tempplayer.hasPermission("towny.command.nation.pact.deny"))) {
                authorised.add(res);
            }
        }
        if (!authorised.isEmpty()) {
            for (Resident res : authorised) {
                TownyMessaging.sendMsg(res.getPlayer(), message);
            }
        }

        leaderMessageQueue.put(targetNation.getUUID(), message);
    }
}
