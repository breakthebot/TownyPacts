package org.breakthebot.townyPacts.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class cancelPact {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return false;
    }
}

// Usage: /n pact break <nation>
// This will break the pact with a specified nation. (broken from 1 side, not both)