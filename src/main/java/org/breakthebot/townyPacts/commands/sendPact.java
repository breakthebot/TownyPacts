package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.townyPacts.pact.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sendPact implements Listener {

    private static final Map<UUID, String> leaderMessageQueue = new HashMap<>();


    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact send <nation> <duration>");
            return false;
        }
        if (!player.hasPermission("towny.command.nation.pact.send")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
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

            if (targetNation.equals(senderNation)) {
                TownyMessaging.sendErrorMsg(player, "You cannot send a pact to your own nation.");
                return false;
            }

            if (MetaData.hasActivePact(senderNation, targetNation)) {
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

            Pact newPact = new Pact(
                    "Pact-" + senderNation.getName() + "-" + targetNation.getName(),
                    senderNation.getName(),
                    targetNation.getName(),
                    durationDays,
                    player.getUniqueId(),
                    null
            );

            MetaData.updatePendingPact(senderNation, newPact);

            TownyMessaging.sendMsg(player, "Pact sent to nation " + targetNationName + " for duration " + durationStr + ".");




            String message = "You have received a pact request from nation " + senderNation.getName() + ".\n" + "Use /n pact accept " + senderNation.getName() + " to accept it.";
            leaderMessageQueue.put(targetNation.getUUID(), message);

            return true;

        } catch (Exception e) {
            TownyMessaging.sendErrorMsg(player, "Towny data not found. Try again later.");
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("towny.command.nation.pact.accept")) { return; }

        Resident res = TownyAPI.getInstance().getResident(player);
        assert res != null;
        if (!res.hasNation()) {
            return;
        }
        Nation nation = res.getNationOrNull();
        assert nation != null;
        UUID uuid = nation.getUUID();
        if (!leaderMessageQueue.containsKey(uuid)) {
            return;
        }

        String message = leaderMessageQueue.remove(uuid);
        TownyMessaging.sendMsg(player, message);
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
