package com.github.frcsty.discordminecrafthook.listener;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.storage.MinecraftGroupProvider;
import com.github.frcsty.discordminecrafthook.storage.RegisteredUserStorage;
import com.github.frcsty.discordminecrafthook.util.Task;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.event.user.track.UserTrackEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UserListener {

    @NotNull
    private final Logger logger;

    @NotNull
    private final MinecraftGroupProvider minecraftGroupProvider;
    @NotNull
    private final RegisteredUserStorage registeredUserStorage;
    @NotNull
    private final LuckPerms luckPerms;

    @NotNull
    private final JDA jda;
    @NotNull
    private final Guild guild;

    /**
     * Handles {@link EventBus} event subscriptions
     *
     * @param plugin Our {@link HookPlugin} instance
     */
    public UserListener(@NotNull final HookPlugin plugin) {
        this.minecraftGroupProvider = plugin.getMinecraftGroupProvider();
        this.registeredUserStorage = plugin.getRegisteredUserStorage();
        this.luckPerms = plugin.getLuckPermsProvider();
        this.jda = plugin.getDiscordProvider().getActiveJDAInstance();
        this.guild = plugin.getDiscordProvider().getLinkedGuild();
        this.logger = plugin.getLogger();

        final EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(UserPromoteEvent.class, this::onUserPromote);
    }

    /**
     * Event fired when a user get's promoted to another group,
     * handles the discord role appropriately
     *
     * @param event {@link UserPromoteEvent}
     */
    private void onUserPromote(final UserPromoteEvent event) {
        handleRole(event);
    }

    /**
     * Event fired when a user get's demoted to another group,
     * handles the discord role appropriately
     *
     * @param event {@link UserDemoteEvent}
     */
    private void onUserDemote(final UserDemoteEvent event) {
        handleRole(event);
    }

    /**
     * Handles our Discord sided roles on Promotes/Demotion
     *
     * @param event A subclass of {@link UserTrackEvent}
     */
    private void handleRole(final UserTrackEvent event) {
        Task.async(() -> {
            final User user = event.getUser();
            final long discordUserID = this.registeredUserStorage.getLinkedUserMemberTagByUUID(user.getUniqueId());
            final net.dv8tion.jda.api.entities.User discordUser = jda.getUserById(discordUserID);

            if (discordUser == null) {
                this.logger.log(Level.WARNING, "Discord User for Player with name '" + user.getFriendlyName() + "' could not be found!");
                return;
            }

            final Group from = getGroupByName(event.getGroupFrom().orElse("invalid"));
            final Group to = getGroupByName(event.getGroupTo().orElse("invalid"));

            final long fromGroup = this.minecraftGroupProvider.getLinkedDiscordRoleID(from);
            final long toGroup = this.minecraftGroupProvider.getLinkedDiscordRoleID(to);

            final Member member = this.guild.getMember(discordUser);

            assert member != null;
            guild.addRoleToMember(member, getRoleByLongId(guild, toGroup)).complete();
            guild.removeRoleFromMember(member, getRoleByLongId(guild, fromGroup)).complete();
        });
    }

    /**
     * Returns a group by name, or null if not present
     *
     * @param groupName Retrieved group name
     * @return A {@link Group} or null if none match the name
     */
    private Group getGroupByName(final String groupName) {
        return this.luckPerms.getGroupManager().getGroup(groupName);
    }

    /**
     * Finds and retrieve a role by a given {@link Long} roleID or null
     *
     * @param guild  Our guild instance
     * @param roleId Our desired role id
     * @return Returns a {@link Role} matching our id, or null
     */
    private Role getRoleByLongId(final Guild guild, final long roleId) {
        final List<Role> roles = guild.getRoles();
        Role result = null;

        for (final Role role : roles) {
            if (role.getIdLong() == roleId) {
                result = role;
                break;
            }
        }

        return result;
    }
}