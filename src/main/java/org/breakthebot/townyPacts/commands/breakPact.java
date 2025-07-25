package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.config;
import org.breakthebot.townyPacts.object.Pact;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.utils.EventHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.townyPacts.utils.MetaData;

import java.util.ArrayList;
import java.util.List;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class breakPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!(player.hasPermission("towny.command.nation.pact.manage") || player.hasPermission("towny.command.nation.pact.break"))) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact break <nation>");
            return false;
        }

        String targetNationName = args[1];
        TownyAPI API = TownyAPI.getInstance();

        Resident resident = API.getResident(player.getUniqueId());
        assert resident != null;
        if (!resident.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You are not part of a nation.");
            return false;
        }

        Nation selfNation;
        selfNation = resident.getNationOrNull();
        assert selfNation != null;

        Nation targetNation = API.getNation(targetNationName);
        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Target nation '" + targetNationName + "' does not exist.");
            return false;
        }

        if (!MetaData.hasActivePact(selfNation, targetNation)) {
            TownyMessaging.sendErrorMsg(player, "No existing pact with " + targetNationName + " to break.");
            return false;
        }

        Pact currentPact = MetaData.getActivePact(selfNation, targetNation);

        if ("BROKEN".equalsIgnoreCase(currentPact.getStatus())) {
            TownyMessaging.sendErrorMsg(player, "This pact has already been broken.");
            return false;
        }

        config settings = TownyPacts.getInstance().getConfiguration();
        int cost = settings.breakPrice;
        if (cost != 0) {

            if (selfNation.getAccount().getHoldingBalance() < cost) {
                TownyMessaging.sendErrorMsg(player, "Your nation does not have enough to break pacts. Needed: " + cost);
                return false;
            }
            selfNation.getAccount().withdraw(cost, "Breaking a pact with " + targetNationName);
        }

        currentPact.breakPact();

        MetaData.updateActivePact(selfNation, currentPact);
        MetaData.updateActivePact(targetNation, currentPact);

        TownyMessaging.sendPrefixedNationMessage(selfNation, "Pact with " + targetNationName + " has been broken by us!");
        TownyMessaging.sendPrefixedNationMessage(targetNation, "Pact with " + selfNation.getName() + " has been broken by them!");

        String message = "The pact with " + selfNation.getName() + " has been broken.";

        EventHelper.addLeaderMessage(targetNation, message);

        return true;
    }
}