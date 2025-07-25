package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.config;
import org.breakthebot.townyPacts.object.Pact;
import org.breakthebot.townyPacts.utils.EventHelper;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class sendPact implements Listener {


    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!(player.hasPermission("towny.command.nation.pact.manage") || player.hasPermission("towny.command.nation.pact.send"))) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }

        if (args.length != 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact send <nation> <duration>");
            return false;
        }


        String targetNationName = args[1];
        String durationStr = args[2];

        try {
            TownyAPI API = TownyAPI.getInstance();
            Resident senderResident = API.getResident(player.getUniqueId());

            if (senderResident == null || !senderResident.hasNation()) {
                TownyMessaging.sendErrorMsg(player, "You must be part of a nation.");
                return false;
            }

            Nation senderNation = senderResident.getNation();
            Nation targetNation = API.getNation(targetNationName);

            if (targetNation == null) {
                TownyMessaging.sendErrorMsg(player, "Nation '" + targetNationName + "' does not exist.");
                return false;
            }
            targetNationName = targetNation.getName(); // Fix capitalisation

            if (targetNation.equals(senderNation)) {
                TownyMessaging.sendErrorMsg(player, "You cannot send a pact to your own nation.");
                return false;
            }

            if (MetaData.hasActivePact(senderNation, targetNation)) {
                TownyMessaging.sendErrorMsg(player, "A pact with " + targetNationName + " already exists.");
                return false;
            }
            if (MetaData.hasPendingPact(senderNation, targetNation)) {
                TownyMessaging.sendErrorMsg(player, "A pending pact with " + targetNationName + " already exists.");
                return false;
            }

            if (senderNation.hasEnemy(targetNation)) {
                TownyMessaging.sendErrorMsg(player, "You cannot send a pact to a nation marked as an enemy.");
                return false;
            }

            int durationDays = parseDurationToDays(durationStr);
            if (durationDays == Integer.MIN_VALUE) {
                TownyMessaging.sendErrorMsg(player, "Invalid duration. Use formats like 1d, 3, or 'forever'.");
                return false;
            }

            config settings = TownyPacts.getInstance().getConfiguration();

            if (settings.pactRequireAlly && !senderNation.hasAlly(targetNation)) {
                TownyMessaging.sendErrorMsg(player, "You must be allied to send a pact!");
                return false;
            }
            int cost = settings.baseCreationPrice;
            if (cost != 0) {
                cost = (cost + 1) / 2;

                if (senderNation.getAccount().getHoldingBalance() < cost) {
                    TownyMessaging.sendErrorMsg(player, "Your nation does not have enough to send pacts. Needed: " + cost);
                    return false;
                }
                senderNation.getAccount().withdraw(cost, "Sending a pact to " + targetNationName);
            }

            Pact newPact = new Pact(
                    "Pact-" + senderNation.getName() + "-" + targetNation.getName(),
                    senderNation.getName(),
                    targetNation.getName(),
                    durationDays,
                    player.getUniqueId(),
                    null
            );

            MetaData.updatePendingPact(senderNation, newPact);
            MetaData.updatePendingPact(targetNation, newPact);

            TownyMessaging.sendMsg(player, "Pact sent to nation " + targetNationName + " for duration " + durationStr + ".");

            String message = "&bYou have received a pact request from nation " + senderNation.getName() + " for duration " + durationStr + ".\n&7Use /n pact accept|deny " + senderNation.getName();

            EventHelper.addLeaderMessage(targetNation, message);
            return true;

        } catch (Exception e) {
            TownyMessaging.sendErrorMsg(player, "Towny data not found. Try again later.");
            return false;
        }
    }

    private static int parseDurationToDays(String duration) {
        if (duration.equalsIgnoreCase("forever")) return -1;
        if (!duration.matches("^\\d+[smwdy]$")) return Integer.MIN_VALUE;

        int number = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
        char unit = duration.charAt(duration.length() - 1);

        return switch (unit) {
            case 'd' -> number;
            case 'w' -> number * 7;
            case 'm' -> number * 30;
            case 'y' -> number * 365;
            default -> Integer.MIN_VALUE;
        };
    }


}
