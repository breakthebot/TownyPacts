package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.townyPacts.pact.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import com.palmergames.bukkit.towny.exceptions.TownyException;

import java.util.List;

public class listPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }
        TownyAPI API = TownyAPI.getInstance();

        Resident res = API.getResident(player.getUniqueId());

        if (res == null || !res.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You are not part of a nation.");
            return false;
        }
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

        if (pacts.isEmpty()) {
            TownyMessaging.sendMsg(player, "Nation has no pacts.");
            return true;
        }

        TownyMessaging.sendMsg(player, "Pacts for nation " + nation.getName() + ":");
        for (Pact pact : pacts) {
            String targetNation = pact.getTargetNation(nation.getName());
            String durationStr = pact.getDuration() == -1 ? "Forever" : pact.getDuration() + " days";
            String status = (pact.getAcceptedBy() == null) ? "Pending" : "Accepted";

            TownyMessaging.sendMsg(player, "- Pact with " + targetNation + " | Duration: " + durationStr + " | Status: " + status);
        }

        return true;
    }
}
