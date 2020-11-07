package com.github.frcsty.discordminecrafthook.bukkit.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
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
@Command("unlink")
public final class UnlinkCommand extends CommandBase {

    @NotNull private final HookPlugin plugin;

    public UnlinkCommand(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission("discordhook.command.unlink")
    public void onCommand(final Player player) {
        final UUID identifier = player.getUniqueId();
        final RegistryUser user = plugin.getRegistryHandler().getRegistryUser(identifier);

        if (user == null) {
            Message.send(
                    player,
                    Property.getByKey("message.not-linked")
            );
            return;
        }

        this.plugin.getRegistryHandler().removeUser(user);
        Message.send(player, Replace.replaceString(
                Property.getByKey("message.user-unlinked"),
                "{member-tag}", user.getMemberTag()
        ));
    }

}
