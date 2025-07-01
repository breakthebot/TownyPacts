package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.config;
import org.breakthebot.townyPacts.object.Pact;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.breakthebot.townyPacts.utils.MetaData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class acceptPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!(player.hasPermission("towny.command.nation.pact.manage") || player.hasPermission("towny.command.nation.pact.accept"))) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact accept <nation>");
            return false;
        }

        String targetNationName = args[1];
        TownyAPI API = TownyAPI.getInstance();

        Resident resident = API.getResident(player.getUniqueId());
        if (resident == null || !resident.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a nation.");
            return false;
        }

        Nation selfNation;
        selfNation = resident.getNationOrNull();
        assert selfNation != null;

        Nation targetNation = API.getNation(targetNationName);
        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + targetNationName + "' not found exist.");
            return false;
        }
        targetNationName = targetNation.getName(); // Fix capitalisation

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

        config settings = TownyPacts.getInstance().getConfiguration();
        int cost = settings.baseCreationPrice;
        if (cost != 0) {
            cost = cost / 2;

            if (selfNation.getAccount().getHoldingBalance() < cost) {
                TownyMessaging.sendErrorMsg(player, "Your nation does not have enough to accept pacts. Needed: " + cost);
                return false;
            }
            selfNation.getAccount().withdraw(cost, "Accepting a pact from " + targetNationName);
        }

        found.setAcceptedBy(player.getUniqueId());
        found.setExpiresAt(found.getExpiresAt() == -1L
                ? -1L
                : found.getCreatedAt() + (found.getDuration() * 86400_000L));

        MetaData.removePendingPact(selfNation, targetNation);
        MetaData.removePendingPact(targetNation, selfNation);
        MetaData.updateActivePact(selfNation, found);
        MetaData.updateActivePact(targetNation, found);

        TownyMessaging.sendMsg(player, "You have accepted the pact with " + targetNationName + "!");
        TownyMessaging.sendPrefixedNationMessage(selfNation, "The Pact with " + targetNationName + " has been accepted!");
        TownyMessaging.sendPrefixedNationMessage(targetNation, "The Pact with " + selfNation.getName() + " has been accepted!");

        leaderMessageQueue.remove(selfNation.getUUID());

        return true;
    }
}