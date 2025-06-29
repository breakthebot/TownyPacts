package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;

import java.util.ArrayList;
import java.util.stream.Collectors;

import java.util.List;

public class pactRegister implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        if (args.length == 0) {
            TownyMessaging.sendErrorMsg(sender, "Usage: /n pact <send|list|break|cancel>");
            return false;
        }

        return switch (args[0].toLowerCase()) {
            case "send" -> sendPact.onCommand(sender, command, label, args);
            case "list" -> listPact.onCommand(sender, command, label, args);
            case "break" -> breakPact.onCommand(sender, command, label, args);
            case "cancel" -> cancelPact.onCommand(sender, command, label, args);
            case "accept" -> acceptPact.onCommand(sender, command, label, args);
            default -> {
                TownyMessaging.sendErrorMsg(player, "Invalid argument. Choose: send, list, break, accept or cancel.");
                yield false;
            }
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("send", "list", "break", "cancel", "accept");
        }

        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 2 && (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("break") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("accept"))) {
            return getOtherNationNames(player);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            return TownyAPI.getInstance().getNations().stream()
                    .map(Nation::getName)
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
            return List.of("1d", "7d", "3w", "2m", "1y", "forever");
        }

        return List.of();
    }

    private List<String> getOtherNationNames(Player player) {
        var api = TownyAPI.getInstance();
        Nation ownNation = null;

        Resident resident = api.getResident(player.getUniqueId());
        if (resident != null && resident.hasNation()) {
            try {
                ownNation = resident.getNation();
            } catch (TownyException e) {
                return List.of();
            }
        }

        Nation finalOwnNation = ownNation;
        return api.getNations().stream()
                .filter(nation -> !nation.equals(finalOwnNation))
                .map(Nation::getName)
                .collect(Collectors.toList());
    }
}