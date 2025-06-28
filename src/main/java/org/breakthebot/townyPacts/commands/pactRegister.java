package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyMessaging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class pactRegister implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
            default -> {
                TownyMessaging.sendErrorMsg(player, "Invalid argument. Choose: send, list, break, or cancel.");
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
            return List.of("send", "list", "break", "cancel");
        }
        return List.of();
    }
}