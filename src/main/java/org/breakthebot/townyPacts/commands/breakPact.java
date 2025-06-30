package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.townyPacts.utils.MetaData;

import static org.breakthebot.townyPacts.commands.pactCommand.leaderMessageQueue;

public class breakPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact break <nation>");
            return false;
        }

        String targetNationName = args[1];
        TownyAPI towny = TownyAPI.getInstance();

        Resident resident = towny.getResident(player.getUniqueId());
        if (resident == null || !resident.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You are not part of a nation.");
            return false;
        }

        Nation selfNation;
        try {
            selfNation = resident.getNation();
        } catch (TownyException e) {
            TownyMessaging.sendErrorMsg(player, "Could not retrieve your nation.");
            return false;
        }

        if (!selfNation.getKing().equals(resident)) {
            TownyMessaging.sendErrorMsg(player, "Only the nation leader can break pacts.");
            return false;
        }

        Nation targetNation = towny.getNation(targetNationName);
        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Target nation '" + targetNationName + "' does not exist.");
            return false;
        }

        boolean hadPact = MetaData.hasActivePact(selfNation, targetNation);
        if (!hadPact) {
            TownyMessaging.sendErrorMsg(player, "No existing pact with " + targetNationName + " to break.");
            return false;
        }

        MetaData.removeActivePact(selfNation, targetNation);
        MetaData.removeActivePact(targetNation, selfNation);

        TownyMessaging.sendMsg(player, "Pact with " + targetNationName + " has been broken on your side.");

        String message = "The pact with " + targetNation.getName() + " has been broken.";
        leaderMessageQueue.put(targetNation.getUUID(), message);

        Resident targetLeader = targetNation.getKing();
        Player targetPlayer = targetLeader.getPlayer();

        if (targetPlayer != null && targetPlayer.isOnline()) {
            TownyMessaging.sendMsg(targetPlayer, message);
        }
        return true;
    }
}