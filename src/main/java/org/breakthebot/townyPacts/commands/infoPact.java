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

import java.util.concurrent.TimeUnit;

public class infoPact {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players can use this command.");
            return false;
        }

        if (!player.hasPermission("towny.command.nation.pact.info")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to view pact information.");
            return false;
        }

        if (args.length < 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact info Pact-<Nation1>-<Nation2>");
            return false;
        }

        String input = args[1];
        String[] parts = input.split("-");

        if (parts.length != 3 || !parts[0].equalsIgnoreCase("Pact")) {
            TownyMessaging.sendErrorMsg(player, "Invalid pact name. Use: Pact-<Nation1>-<Nation2>");
            return false;
        }

        String nation1 = parts[1];
        String nation2 = parts[2];
        TownyAPI API = TownyAPI.getInstance();
        Nation nat1 = API.getNation(nation1);
        Nation nat2 = API.getNation(nation2);
        if (nat1 == null) {
            TownyMessaging.sendErrorMsg(player, "Nation of '" + nation1 + "' not found.");
            return false;
        }
        if (nat2 == null) {
            TownyMessaging.sendErrorMsg(player, "Nation of '" + nation2 + "' not found.");
            return false;
        }
        boolean hasActive = MetaData.hasActivePact(nat1, nat2);
        boolean hasPending = MetaData.hasPendingPact(nat1, nat2);
        if (!( hasActive || hasPending)) {
            TownyMessaging.sendErrorMsg(player, "The nations of '" + nation1 + "' & '" + nation2 + "' don't have an active or pending pact.");
            return false;
        }
        Pact pact;
        if (hasActive) {
            pact = MetaData.getActivePact(nat1, nat2);
        } else {
            pact = MetaData.getPendingPact(nat1, nat2);
        }
        Resident res1 = API.getResident(pact.getSentBy());
        assert res1 != null;
        String senderName = res1.getName();
        Resident res2 = API.getResident(pact.getAcceptedBy());
        assert res2 != null;
        String acceptedByName = res2.getName();

        long exp = pact.getExpiresAt();
        String time = (exp == -1) ? "Forever" : TimeUnit.MILLISECONDS.toDays(exp - System.currentTimeMillis()) + "d "
                + TimeUnit.MILLISECONDS.toHours(exp - System.currentTimeMillis()) % 24 + "h";

        String info = "\nSignatories: " + senderName + ", " + acceptedByName
                + "\nDuration: " + pact.getDuration()
                + "\nStatus: " + pact.getStatus()
                + "\nExpires in: " + time;
        TownyMessaging.sendMsg(player, "Pact information for " + pact.getName() + info);

        return true;
    }
}
