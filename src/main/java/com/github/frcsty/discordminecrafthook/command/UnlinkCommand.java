package com.github.frcsty.discordminecrafthook.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.storage.RegisteredUserStorage;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Task;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command("unlink")
public final class UnlinkCommand extends CommandBase {

    @NotNull
    private final HookPlugin plugin;
    @NotNull
    private final RegisteredUserStorage registeredUserStorage;
    @NotNull
    private final ConfigStorage configStorage;

    public UnlinkCommand(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
        this.registeredUserStorage = plugin.getRegisteredUserStorage();
        this.configStorage = plugin.getConfigStorage();
    }

    @Default
    @Permission("discordhook.command.unlink")
    public void onCommand(final Player player) {
        Task.async(() -> {
            final LinkedUser linkedUser = this.registeredUserStorage.getLinkedUserByMinecraftUUID(player.getUniqueId());
            if (linkedUser == null) {
                Message.send(
                        player,
                        this.configStorage.getConfigString("messages.not-linked")
                );
                return;
            }

            this.registeredUserStorage.invalidateUser(plugin, linkedUser);
            Message.send(
                    player,
                    this.configStorage.getConfigString("messages.user-unlinked-mc")
            );
        });
    }

}
