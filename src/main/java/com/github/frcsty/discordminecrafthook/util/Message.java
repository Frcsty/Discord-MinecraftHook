package com.github.frcsty.discordminecrafthook.util;

import me.mattstudios.mfmsg.bukkit.BukkitMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Message {

    private static final BukkitMessage MESSAGE = BukkitMessage.create();

    public static void send(@NotNull final CommandSender sender, final String input) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            MESSAGE.parse(input).sendMessage(player);
        } else {
            sender.sendMessage(MESSAGE.parse(input).toString());
        }
    }

}
