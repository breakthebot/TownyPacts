package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyPacts.object.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class denyPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!(player.hasPermission("towny.command.nation.pact.manage") || player.hasPermission("towny.command.nation.pact.deny"))) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to deny pacts.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact deny <nation>");
            return false;
        }

        String senderNationName = args[1];

        TownyAPI API = TownyAPI.getInstance();

        Resident res = API.getResident(player);
        assert res != null;
        if (!res.hasNation()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a nation.");
            return false;
        }

        Nation selfNation = res.getNationOrNull();
        assert selfNation != null;
        Nation targetNation = API.getNation(senderNationName);

        if (targetNation == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + senderNationName + "' not found.");
            return false;
        }

        Pact pending = MetaData.getPendingPact(selfNation, targetNation);

        if (pending == null) {
            TownyMessaging.sendErrorMsg(player, "No pending pact from nation " + senderNationName + ".");
            return false;
        }

        MetaData.removePendingPact(selfNation, targetNation);
        MetaData.removePendingPact(targetNation, selfNation);

        TownyMessaging.sendPrefixedNationMessage(selfNation, "You have denied the pact from nation " + senderNationName + ".");
        TownyMessaging.sendPrefixedNationMessage(targetNation, "The nation of " + selfNation.getName() + " has denied our Pact request.");

        return true;
    }
}