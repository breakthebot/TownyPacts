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

        if (args.length < 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /n pact info <Nation1> <Nation2>");
            return false;
        }

        String nationName1 = args[1];
        String nationName2 = args[2];

        TownyAPI API = TownyAPI.getInstance();
        Nation nat1 = API.getNation(nationName1);
        Nation nat2 = API.getNation(nationName2);

        if (nat1 == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + nationName1 + "' not found.");
            return false;
        }

        if (nat2 == null) {
            TownyMessaging.sendErrorMsg(player, "Nation '" + nationName2 + "' not found.");
            return false;
        }

        boolean hasActive = MetaData.hasActivePact(nat1, nat2) || MetaData.hasActivePact(nat2, nat1);
        boolean hasPending = MetaData.hasPendingPact(nat1, nat2) || MetaData.hasPendingPact(nat2, nat1);

        if (!(hasActive || hasPending)) {
            TownyMessaging.sendErrorMsg(player, "The nations of '" + nationName1 + "' & '" + nationName2 + "' don't have an active or pending pact.");
            return false;
        }

        // Get the pact in either direction
        Pact pact;
        if (hasActive) {
            pact = MetaData.getActivePact(nat1, nat2);
            if (pact == null) {
                pact = MetaData.getActivePact(nat2, nat1);
            }
        } else {
            pact = MetaData.getPendingPact(nat1, nat2);
            if (pact == null) {
                pact = MetaData.getPendingPact(nat2, nat1);
            }
        }

        if (pact == null) {
            TownyMessaging.sendErrorMsg(player, "Could not retrieve pact information.");
            return false;
        }

        Resident senderRes;
        Resident acceptedRes = API.getResident(pact.getAcceptedBy());

        try {
            if (pact.getSentBy() == null) {
                TownyMessaging.sendErrorMsg(player, "Pact sender information is missing.");
                return false;
            }
            senderRes = API.getResident(pact.getSentBy());
            if (senderRes == null) {
                TownyMessaging.sendErrorMsg(player, "Could not retrieve sender signatory information.");
                return false;
            }

            if (pact.getStatus().equalsIgnoreCase("active")) {
                if (pact.getAcceptedBy() == null) {
                    TownyMessaging.sendErrorMsg(player, "Pact accepted-by information is missing.");
                    return false;
                }
            }
        } catch (Exception e) {
            TownyMessaging.sendErrorMsg(player, "Could not retrieve pact information.");
            return false;
        }

        String info = "\n&bSignatories: &3" + senderRes.getName();
        if (acceptedRes != null) {
            info += ", " + acceptedRes.getName();
        } else if (pact.getStatus().equalsIgnoreCase("pending")) {
            info += " &7(pending acceptance)";
        }
        long exp = pact.getExpiresAt();
        String status = pact.getStatus();
        if (status.equalsIgnoreCase("pending")) {
            status = "&7" + status;
        } else if (status.equalsIgnoreCase("broken")) {
            status = "&4" + status;
        }

        info += "\n&bDuration: &7" + (exp == -1 ? "&6Forever" : pact.getDuration())
                + "\n&bStatus: " + status;

        if (exp == -1) {
            info += "\n&bExpires: &6Never";
        } else {
            long remainingMillis = exp - System.currentTimeMillis();
            if (remainingMillis < 0) remainingMillis = 0;
            String time = TimeUnit.MILLISECONDS.toDays(remainingMillis) + "d "
                    + (TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24) + "h";
            info += "\n&bExpires: &7in " + time;
        }

        TownyMessaging.sendMsg(player, "&bPact information: \n&bName: &7" + pact.getName() + info);
        return true;
    }
}
