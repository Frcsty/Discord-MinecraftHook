package com.github.frcsty.discordminecrafthook.storage;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class MinecraftGroupProvider {

    @NotNull private final RegisteredUserStorage registeredUserStorage;
    @NotNull private final Map<Group, Long> linkedGroups = new HashMap<>();
    @NotNull private final HookPlugin plugin;
    @NotNull private final LuckPerms luckPerms;

    public MinecraftGroupProvider(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
        this.registeredUserStorage = plugin.getRegisteredUserStorage();
        this.luckPerms = plugin.getLuckPermsProvider();
    }

    public void initializeRunnable() {
        final long delay = Long.valueOf(plugin.getConfigStorage().getConfigString("settings.sync-delay"));

        new BukkitRunnable() {
            @Override
            public void run() {
                final Guild guild = plugin.getDiscordProvider().getLinkedGuild();
                final UserManager userManager = luckPerms.getUserManager();

                for (final long memberID : registeredUserStorage.getStorage()) {
                    final LinkedUser linkedUser = registeredUserStorage.getLinkedUserByMemberTag(memberID);

                    if (linkedUser == null) {
                        plugin.getLogger().log(Level.WARNING, "LinkedUser for Discord user with identifier '" + memberID + "' is null, skipping Role-Sync");
                        continue;
                    }

                    final Member member = guild.getMemberById(memberID);
                    final CompletableFuture<User> user = userManager.loadUser(linkedUser.getMinecraftIdentifier());
                    user.thenAcceptAsync(asyncUser -> {
                        final String primaryGroup = asyncUser.getPrimaryGroup();

                        for (final Group group : linkedGroups.keySet()) {
                            final long groupRole = linkedGroups.get(group);
                            final Role role = guild.getRoleById(groupRole);

                            if (primaryGroup.equalsIgnoreCase(group.getName()) && !member.getRoles().contains(role)) {
                                guild.getController().addRolesToMember(member, role).complete();
                                continue;
                            }

                            guild.getController().removeRolesFromMember(member, role).complete();
                        }
                        /*
                        final ImmutableContextSet contextSet = luckPerms.getContextManager().getStaticContext();
                        final QueryOptions queryOptions = luckPerms.getContextManager().getStaticQueryOptions();

                        final Collection<Group> groups = asyncUser.getInheritedGroups(queryOptions);
                        groups.forEach(group -> {
                            final Long linkedRole =

                        });
                        */
                    });
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, delay);
    }

    /**
     * If the {@link ConfigurationSection} for defining groups can not be found, it is generated,
     * if the section is present it retrieves the {@link Group}'s from the LuckPerms {@link GroupManager} and
     * loads them to our linkedGroups {@link Map} alongside it's respective Discord Role ID
     */
    public void setupConfig() {
        final GroupManager groupManager = luckPerms.getGroupManager();
        final FileConfiguration configuration = this.plugin.getConfig();

        if (configuration.getConfigurationSection("minecraft-groups") == null) {
            final ConfigurationSection section = configuration.createSection("minecraft-groups");

            for (final Group group : groupManager.getLoadedGroups()) {
                section.set(group.getName(), "<set designated role ID>");
            }

            this.plugin.saveConfig();
            this.plugin.getLogger().log(Level.INFO, "Generated configuration section 'minecraft-groups', populate it with their respective Discord Role IDs.");
            return;
        }

        final ConfigurationSection section = configuration.getConfigurationSection("minecraft-groups");
        if (section == null) {
            this.plugin.getLogger().log(Level.WARNING, "An issue has occurred while retrieving Linked Groups from the configuration!");
            return;
        }
        for (final String groupName : section.getKeys(false)) {
            final Group group = groupManager.getGroup(groupName);

            if (group == null) {
                this.plugin.getLogger().log(Level.WARNING, "Could not find a matching group for group name '" + groupName + "'!");
                continue;
            }

            this.linkedGroups.put(group, section.getLong(groupName));
        }
    }

    /**
     * Returns the linked discord role ID, or 0 if it doesn't exist
     *
     * @param group User's {@link Group} to be assigned in discord
     * @return  Linked discord role ID, or 0 if not present
     */
    public long getLinkedDiscordRoleID(final Group group) {
        return this.linkedGroups.get(group);
    }

    /**
     * Returns the linked discord role ID or 0 if the {@link Group} is invalid,
     * or the linked role can not be found
     *
     * @param groupName User's {@link Group} to be assigned as a {@link String}
     * @return  Linked discord role ID, or 0 if invalid group, or not present
     */
    public long getLinkedDiscordRoleID(final String groupName) {
        final Group group = this.luckPerms.getGroupManager().getGroup(groupName);

        return getLinkedDiscordRoleID(group);
    }

}
