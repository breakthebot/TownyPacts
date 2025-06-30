package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.pact.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class denyPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact deny <nation>");
            return false;
        }

        if (!player.hasPermission("towny.command.nation.pact.deny")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to deny pacts.");
            return false;
        }

        String senderNationName = args[1];

        Resident res = TownyAPI.getInstance().getResident(player.getUniqueId());
        if (res == null || !res.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a nation to deny pacts.");
            return false;
        }

        Nation selfNation = res.getNationOrNull();
        Nation targetNation = TownyAPI.getInstance().getNation(senderNationName);

        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + senderNationName + "' not found.");
            return false;
        }

        List<Pact> yourPendingPacts = MetaData.getPendingPacts(selfNation);
        boolean hasPendingFromSender = yourPendingPacts.stream()
                .anyMatch(p -> {
                    if (p.getTargetNation(targetNation.getName()) == null) return false;
                    assert selfNation != null;
                    return p.getTargetNation(targetNation.getName()).equalsIgnoreCase(selfNation.getName());
                });

        if (!hasPendingFromSender) {
            TownyMessaging.sendErrorMsg(player, "No pending pact from nation " + senderNationName + ".");
            return false;
        }

        MetaData.removePendingPact(selfNation, targetNation);
        MetaData.removePendingPact(targetNation, selfNation);

        TownyMessaging.sendMsg(player, "You have denied the pact from nation " + senderNationName + ".");

        String message = "The pact with " + targetNation.getName() + " has been denied.";
        leaderMessageQueue.put(targetNation.getUUID(), message);

        Resident targetLeader = targetNation.getKing();
        Player targetPlayer = targetLeader.getPlayer();

        if (targetPlayer != null && targetPlayer.isOnline()) {
            TownyMessaging.sendMsg(targetPlayer, message);
        }
        return true;
    }
}