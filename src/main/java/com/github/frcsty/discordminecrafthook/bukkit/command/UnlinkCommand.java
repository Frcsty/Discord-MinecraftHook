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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

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

        final Guild guild = plugin.getLinkedDiscordGuild();
        final User luckPermsUser = plugin.getLuckPermsProvider().getUserManager().getUser(identifier);
        SyncRankCommand.executeRoleCheck(plugin, guild, player, luckPermsUser, user);
        this.plugin.getRegistryHandler().removeUser(user);
        Message.send(player, Replace.replaceString(
                Property.getByKey("message.user-unlinked"),
                "{member-tag}", user.getMemberTag()
        ));

        final net.dv8tion.jda.api.entities.Role syncRole = com.github.frcsty.discordminecrafthook.util.Role.getRoleByLongId(guild, Long.valueOf(Property.getByKey("settings.discord-sync-role")));
        final Member member = guild.getMemberById(user.getMemberID());

        if (syncRole == null) {
            plugin.getLogger().log(Level.WARNING, "Specified role was null, please double check config settings!");
            return;
        }
        if (member == null) {
            plugin.getLogger().log(Level.WARNING, "Discord Member for Player with Name '" + player.getName() + "' was null!");
            return;
        }

        plugin.getLinkedDiscordGuild().removeRoleFromMember(member, syncRole).complete();
    }

}
