package com.github.frcsty.discordminecrafthook.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.cache.CodeBuilder;
import com.github.frcsty.discordminecrafthook.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Replace;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Command("verify")
public final class VerifyCommand extends CommandBase {

    @NotNull private final ConfigStorage configStorage;
    @NotNull private final RequestCache cache;

    public VerifyCommand(@NotNull final HookPlugin plugin) {
        this.configStorage = plugin.getConfigStorage();
        this.cache = plugin.getRequestCache();
    }

    @Default
    @Permission("discordhook.command.verify")
    public void onCommand(final Player player) {
        final UUID identifier = player.getUniqueId();
        this.cache.invalidateUserCodes(identifier);

        final String generatedCode = CodeBuilder.getRandomCode(
                this.configStorage.getConfigString("settings.code-size")
        );

        this.cache.addCodeToCache(generatedCode, identifier);
        Message.send(player, Replace.replaceString(
                        this.configStorage.getConfigString("messages.generated-code"),
                "{generated-code}", generatedCode
        ));
    }

}
