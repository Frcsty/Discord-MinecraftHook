package com.github.frcsty.discordminecrafthook.bukkit.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.data.cache.CodeBuilder;
import com.github.frcsty.discordminecrafthook.data.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.data.registry.RegistryHandler;
import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Property;
import com.github.frcsty.discordminecrafthook.util.Replace;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
@Command("verify")
public final class VerifyCommand extends CommandBase {

    @NotNull private final RegistryHandler registryHandler;
    @NotNull private final RequestCache requestCache;

    public VerifyCommand(@NotNull final HookPlugin plugin) {
        this.registryHandler = plugin.getRegistryHandler();
        this.requestCache = plugin.getRequestCache();
    }

    @Default
    @Permission("discordhook.command.verify")
    public void onCommand(final Player player) {
        final UUID identifier = player.getUniqueId();
        final RegistryUser user = this.registryHandler.getRegistryUser(identifier);

        if (user != null) {
            Message.send(player, Replace.replaceString(
                    Property.getByKey("message.account-already-linked"),
                    "{member-tag}", user.getMemberTag()
            ));
            return;
        }

        this.requestCache.invalidateUserCodes(identifier);
        final String generatedCode = CodeBuilder.getRandomCode(
                Property.getByKey("settings.code-size")
        );

        this.requestCache.addCodeToCache(generatedCode, identifier);
        Message.send(player, Replace.replaceString(
                Property.getByKey("message.generated-code"),
                "{generated-code}", generatedCode),
                Property.getByKey("message.code-tooltip"),
                generatedCode
        );
    }

}
