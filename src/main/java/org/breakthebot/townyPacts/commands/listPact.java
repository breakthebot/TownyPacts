package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.townyPacts.pact.pactObject;
import org.breakthebot.townyPacts.utils.MetaData;
import com.palmergames.bukkit.towny.exceptions.TownyException;

import java.util.List;

public class listPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());

        if (resident == null || !resident.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You are not part of a nation.");
            return false;
        }

        Nation nation;
        try {
            nation = resident.getNation();
        } catch (TownyException e) {
            TownyMessaging.sendErrorMsg(player, "Your nation data could not be retrieved.");
            return false;
        }

        List<pactObject> pacts = MetaData.getPacts(nation);

        if (pacts.isEmpty()) {
            TownyMessaging.sendMsg(player, "Your nation has no pacts.");
            return true;
        }

        TownyMessaging.sendMsg(player, "Pacts for nation " + nation.getName() + ":");
        for (pactObject pact : pacts) {
            String targetNation = pact.getTargetNation(nation.getName());
            String durationStr = pact.getDuration() == -1 ? "Forever" : pact.getDuration() + " days";
            String status = (pact.getAcceptedBy() == null) ? "Pending" : "Accepted";

            TownyMessaging.sendMsg(player, "- Pact with " + targetNation + " | Duration: " + durationStr + " | Status: " + status);
        }

        return true;
    }
}
