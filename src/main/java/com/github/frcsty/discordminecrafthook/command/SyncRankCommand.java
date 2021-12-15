package com.github.frcsty.discordminecrafthook.command;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.storage.MinecraftGroupProvider;
import com.github.frcsty.discordminecrafthook.storage.RegisteredUserStorage;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Task;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@Command("syncrank")
@Alias("ranksync")
public final class SyncRankCommand extends CommandBase {

    @NotNull
    private final HookPlugin plugin;
    @NotNull
    private final RegisteredUserStorage registeredUserStorage;
    @NotNull
    private final ConfigStorage configStorage;
    @NotNull
    private final MinecraftGroupProvider groupProvider;

    public SyncRankCommand(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
        this.registeredUserStorage = plugin.getRegisteredUserStorage();
        this.configStorage = plugin.getConfigStorage();
        this.groupProvider = plugin.getMinecraftGroupProvider();
    }

    @Default
    @Permission("discordhook.command.syncrank")
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

            final Guild guild = plugin.getDiscordProvider().getLinkedGuild();
            final UserManager userManager = plugin.getLuckPermsProvider().getUserManager();
            final Member member = guild.getMemberById(linkedUser.getDiscordIdentifier());
            final CompletableFuture<User> user = userManager.loadUser(linkedUser.getMinecraftIdentifier());
            user.thenAcceptAsync(asyncUser -> {
                final String primaryGroup = asyncUser.getPrimaryGroup();

                for (final Group group : groupProvider.getLinkedGroups().keySet()) {
                    final long groupRole = groupProvider.getLinkedGroups().get(group);
                    final Role role = guild.getRoleById(groupRole);

                    if (primaryGroup.equalsIgnoreCase(group.getName())) {
                        assert member != null;
                        if (!member.getRoles().contains(role)) {
                            assert role != null;
                            guild.addRoleToMember(member, role).complete();
                            continue;
                        }
                    }

                    assert role != null;
                    assert member != null;
                    guild.removeRoleFromMember(member, role).complete();
                }

                /*
                final ImmutableContextSet contextSet = plugin.getLuckPermsProvider().getContextManager().getStaticContext();
                final QueryOptions queryOptions = plugin.getLuckPermsProvider().getContextManager().getStaticQueryOptions();

                final Collection<Group> groups = asyncUser.getInheritedGroups(queryOptions);
                groups.forEach(group -> {
                    final Long linkedRole =

                });
                 */
            });
        });
    }

}
