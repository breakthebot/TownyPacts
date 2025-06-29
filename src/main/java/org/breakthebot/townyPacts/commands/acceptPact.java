package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.pact.Pact;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.breakthebot.townyPacts.utils.MetaData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class acceptPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact accept <nation>");
            return false;
        }

        if (!player.hasPermission("towny.command.nation.pact.accept")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }

        String targetNationName = args[1];
        TownyAPI towny = TownyAPI.getInstance();

        Resident resident = towny.getResident(player.getUniqueId());
        if (resident == null || !resident.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a nation.");
            return false;
        }

        Nation selfNation;
        selfNation = resident.getNationOrNull();
        assert selfNation != null;

        Nation targetNation = towny.getNation(targetNationName);
        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + targetNationName + "' not found exist.");
            return false;
        }

        List<Pact> pendingPacts = MetaData.getPendingPacts(targetNation);
        Pact found = null;

        for (Pact pact : pendingPacts) {
            if (pact.getTargetNation(targetNation.getName()).equalsIgnoreCase(selfNation.getName())
                    && pact.getAcceptedBy() == null) {
                found = pact;
                break;
            }
        }

        if (found == null) {
            TownyMessaging.sendErrorMsg(player, "No pending pacts from " + targetNationName);
            return false;
        }

        found.setAcceptedBy(player.getUniqueId());

        MetaData.removePendingPact(selfNation, targetNation);
        MetaData.updateActivePact(selfNation, found);
        MetaData.updateActivePact(targetNation, found);

        TownyMessaging.sendMsg(player, "You have accepted the pact with " + targetNationName + "!");
        return true;
    }
}