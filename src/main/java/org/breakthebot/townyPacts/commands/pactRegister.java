package org.breakthebot.townyPacts.commands;

import com.palmergames.bukkit.towny.TownyMessaging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class pactRegister implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        switch(args[0]) {
            case "send" -> {
                return sendPact.onCommand(sender, command, label, args);
            }
            case "break" -> {
                return breakPact.onCommand(sender, command, label, args);
            }
            case "cancel" -> {
                return cancelPact.onCommand(sender, command, label, args);
            }
            default -> {
                TownyMessaging.sendErrorMsg(player, "Invalid argument. Choose: send, break, or cancel.");
                return false;
            }
        }
    }
}