package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.breakthebot.townyPacts.pact.pactObject;
import org.breakthebot.townyPacts.utils.MetaData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

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
            TownyMessaging.sendErrorMsg(player, "Only the nation leader can accept pacts.");
            return false;
        }

        Nation targetNation = towny.getNation(targetNationName);
        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Target nation '" + targetNationName + "' does not exist.");
            return false;
        }

        List<pactObject> targetPacts = MetaData.getPacts(targetNation);
        pactObject found = null;

        for (pactObject pact : targetPacts) {
            if (pact.getTargetNation(targetNation.getName()).equalsIgnoreCase(selfNation.getName())
                    && pact.getAcceptedBy() == null) {
                found = pact;
                break;
            }
        }

        if (found == null) {
            TownyMessaging.sendErrorMsg(player, "No pending pact request from " + targetNationName + " to accept.");
            return false;
        }

        found.setAcceptedBy(player.getUniqueId());

        MetaData.addOrUpdatePact(selfNation, found);
        MetaData.addOrUpdatePact(targetNation, found);

        TownyMessaging.sendMsg(player, "You have accepted the pact with " + targetNationName + ".");
        return true;
    }
}