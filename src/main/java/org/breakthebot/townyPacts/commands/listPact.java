package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.townyPacts.object.Pact;
import org.breakthebot.townyPacts.utils.MetaData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class listPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!player.hasPermission("towny.command.nation.pact.list")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to deny pacts.");
            return false;
        }

        TownyAPI API = TownyAPI.getInstance();

        Resident res = API.getResident(player.getUniqueId());
        assert res != null;

        Nation nation;
        if (args.length == 1) {
            nation = res.getNationOrNull();
        } else {
            nation = API.getNation(args[1]);
        }
        if (nation == null) {
            TownyMessaging.sendErrorMsg(player, "Nation not found.");
            return false;
        }


        List<Pact> pacts = MetaData.getActivePacts(nation);
        pacts.addAll(MetaData.getPendingPacts(nation));

        if (pacts.isEmpty()) {
            TownyMessaging.sendMsg(player, "Nation " + nation.getName() + " has no pacts.");
            return true;
        }

        List<Pact> active = new ArrayList<>(), pending = new ArrayList<>(), broken = new ArrayList<>();
        for (Pact pact : pacts) {
            switch (pact.getStatus().toUpperCase()) {
                case "ACTIVE" -> active.add(pact);
                case "PENDING" -> pending.add(pact);
                case "BROKEN" -> broken.add(pact);
            }
        }

        Comparator<Pact> byExpiry = Comparator.comparingLong(p -> p.getExpiresAt() == -1 ? Long.MAX_VALUE : p.getExpiresAt());
        List<List<Pact>> categories = List.of(active, pending, broken);
        String[] headers = {"&2Active Pacts:", "&ePending Pacts:", "&cBroken Pacts:"};

        for (int i = 0; i < 3; i++) {
            List<Pact> list = categories.get(i);
            list.sort(byExpiry.reversed());
            if (!list.isEmpty()) TownyMessaging.sendMsg(player, headers[i]);

            for (Pact pact : list) {
                String target = pact.getTargetNation(nation.getName());
                long exp = pact.getExpiresAt();
                String time = (exp == -1) ? "Never" : "in " + TimeUnit.MILLISECONDS.toDays(exp - System.currentTimeMillis()) + "d "
                        + TimeUnit.MILLISECONDS.toHours(exp - System.currentTimeMillis()) % 24 + "h";
                TownyMessaging.sendMsg(player, "&b- Pact with " + target + " | &7Expires: " + time);
            }
        }


        return true;
    }
}
