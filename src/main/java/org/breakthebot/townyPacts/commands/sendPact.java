package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.pact.pactObject;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class sendPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact send <nation> <duration>");
            return false;
        }

        String targetNationName = args[1];
        String durationStr = args[2];

        try {
            TownyAPI towny = TownyAPI.getInstance();
            Resident senderResident = towny.getResident(player.getUniqueId());

            if (senderResident == null || !senderResident.hasNation()) {
                TownyMessaging.sendErrorMsg(player, "You are not part of a nation.");
                return false;
            }

            Nation senderNation = senderResident.getNation();

            if (!senderNation.getKing().equals(senderResident)) {
                TownyMessaging.sendErrorMsg(player, "You must be the leader of your nation to send a pact.");
                return false;
            }

            Nation targetNation = towny.getNation(targetNationName);

            if (targetNation == null) {
                TownyMessaging.sendErrorMsg(player, "Nation '" + targetNationName + "' does not exist.");
                return false;
            }

            if (targetNation.equals(senderNation)) {
                TownyMessaging.sendErrorMsg(player, "You cannot send a pact to your own nation.");
                return false;
            }

            if (MetaData.hasPact(senderNation, targetNation)) {
                TownyMessaging.sendErrorMsg(player, "A pact with " + targetNationName + " already exists.");
                return false;
            }

            if (senderNation.hasEnemy(targetNation)) {
                TownyMessaging.sendErrorMsg(player, "You cannot send a pact to a nation marked as an enemy.");
                return false;
            }

            int durationDays = parseDurationToDays(durationStr);
            if (durationDays == Integer.MIN_VALUE) {
                TownyMessaging.sendErrorMsg(player, "Invalid duration. Use formats like 1d, 2w, or 'forever'.");
                return false;
            }

            pactObject newPact = new pactObject(
                    "Pact-" + senderNation.getName() + "-" + targetNation.getName(),
                    senderNation.getName(),
                    targetNation.getName(),
                    durationDays,
                    player.getUniqueId(),
                    null
            );

            MetaData.addOrUpdatePact(senderNation, newPact);

            TownyMessaging.sendMsg(player, "Pact sent to nation " + targetNationName + " for duration " + durationStr + ".");

            Resident targetLeader = targetNation.getKing();
            Player targetPlayer = targetLeader.getPlayer();

            if (targetPlayer != null && targetPlayer.isOnline()) {
                TownyMessaging.sendMsg(targetPlayer,
                        "You have received a pact request from nation " + senderNation.getName() + ".\n" +
                                "Use '/n pact accept " + senderNation.getName() + "' to accept it."
                );
            }

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
