package com.github.frcsty.discordminecrafthook.util;

import me.clip.placeholderapi.libs.JSONMessage;
import me.mattstudios.mfmsg.bukkit.BukkitMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Message {

    private static final BukkitMessage MESSAGE = BukkitMessage.create();

    public static void send(@NotNull final CommandSender sender, @NotNull final String input) {
        sender.sendMessage(MESSAGE.parse(input).toString());
    }

    public static void send(@NotNull final Player player, @NotNull final String input, @NotNull final String tooltip, @NotNull final String code) {
        final JSONMessage jsonMessage = JSONMessage.create(MESSAGE.parse(input).toString());

        jsonMessage.tooltip(MESSAGE.parse(tooltip).toString());
        jsonMessage.suggestCommand("-verify " + code);
        jsonMessage.send(player);
    }

}
