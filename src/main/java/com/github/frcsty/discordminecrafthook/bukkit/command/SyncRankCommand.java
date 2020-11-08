package com.github.frcsty.discordminecrafthook.bukkit.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Property;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@SuppressWarnings("unused")
@Command("ranksync")
public final class SyncRankCommand extends CommandBase {

    @NotNull
    private final HookPlugin plugin;

    public SyncRankCommand(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
    }

    public static void executeRoleCheck(final HookPlugin plugin, final Guild guild, final Player player, final User luckPermsUser, final RegistryUser registryUser) {
        CompletableFuture.supplyAsync(() -> {
            final Member discordMember = guild.getMemberById(registryUser.getMemberID());

            if (discordMember == null) {
                plugin.getLogger().log(Level.SEVERE, "Discord member for RegistryUser with Name '" + registryUser.getMinecraftUsername() + "' is null!");
                return null;
            }
            for (final Role role : discordMember.getRoles()) {
                final Group group = getGroupByRoleID(plugin, role.getIdLong());

                if (group == null) {
                    continue;
                }
                if (!player.hasPermission("group." + group.getName())) {
                    guild.removeRoleFromMember(discordMember, role).complete();
                }
            }

            for (final Group group : plugin.getRoleAssociations().keySet()) {
                final Role role = getRoleByGroup(plugin, group);

                if (discordMember.getRoles().contains(role)) {
                    continue;
                }
                if (!player.hasPermission("group." + group.getName())) {
                    continue;
                }

                guild.addRoleToMember(discordMember, role).complete();
            }
            return null;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Returns a {@link Role} associated to the given {@link Group}
     *
     * @param plugin Our {@link HookPlugin} instance
     * @param group  Desired group
     * @return Role associated to the group or null
     */
    private static Role getRoleByGroup(final HookPlugin plugin, final Group group) {
        return plugin.getLinkedDiscordGuild().getRoleById(plugin.getRoleAssociations().get(group));
    }

    /**
     * Returns a group or null if not found from the associated roles
     *
     * @param plugin Our {@link HookPlugin} instance
     * @param roleID Role ID
     * @return Returns a {@link Group} or null
     */
    private static Group getGroupByRoleID(final HookPlugin plugin, final long roleID) {
        final Map<Group, Long> associatedRoles = plugin.getRoleAssociations();
        Group group = null;

        for (final Group associatedGroup : associatedRoles.keySet()) {
            final long associatedGroupRole = associatedRoles.get(associatedGroup);

            if (associatedGroupRole == roleID) {
                group = associatedGroup;
                break;
            }
        }

        return group;
    }

    @Default
    @Permission("discordhook.command.ranksync")
    public void onCommand(final Player player) {
        final UUID identifier = player.getUniqueId();
        final RegistryUser user = this.plugin.getRegistryHandler().getRegistryUser(identifier);

        if (user == null) {
            Message.send(
                    player,
                    Property.getByKey("message.not-linked")
            );
            return;
        }

        Message.send(
                player,
                Property.getByKey("message.roles-synced")
        );
        final User luckPermsUser = plugin.getLuckPermsProvider().getUserManager().getUser(identifier);
        executeRoleCheck(plugin, plugin.getLinkedDiscordGuild(), player, luckPermsUser, user);
    }

}
