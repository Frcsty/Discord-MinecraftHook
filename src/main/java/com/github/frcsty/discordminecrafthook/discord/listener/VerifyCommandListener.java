package com.github.frcsty.discordminecrafthook.discord.listener;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.storage.RegisteredUserStorage;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Replace;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class VerifyCommandListener extends ListenerAdapter {

    @NotNull private final HookPlugin plugin;
    @NotNull private final ConfigStorage configStorage;
    @NotNull private final RequestCache requestCache;
    @NotNull private final RegisteredUserStorage registeredUserStorage;

    public VerifyCommandListener(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
        this.configStorage = plugin.getConfigStorage();
        this.requestCache = plugin.getRequestCache();
        this.registeredUserStorage = plugin.getRegisteredUserStorage();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();
        final Guild guild = event.getGuild();
        final String content = event.getMessage().getContentRaw();
        if (!content.startsWith("-verify") && !content.startsWith(guild.getSelfMember().getAsMention())) {
            return;
        }

        final User author = event.getAuthor();
        if (author.isBot()) return;

        final Member member = event.getMember();
        if (this.registeredUserStorage.getLinkedUserByMemberTag(member.getUser().getIdLong()) != null) {
            channel.sendMessage(
                    this.configStorage.getConfigString("messages.already-linked")
            ).queue();
            return;
        }

        final String[] arguments = content.split(" ");

        if (arguments.length < 2) {
            channel.sendMessage(
                    this.configStorage.getConfigString("messages.invalid-params")
            ).queue();
            return;
        }

        final String enteredCode = arguments[1];
        final UUID minecraftUserIdentifier = this.requestCache.getUUIDAssociatedTo(enteredCode);

        if (minecraftUserIdentifier == null) {
            channel.sendMessage(
                    this.configStorage.getConfigString("messages.invalid-code")
            ).queue();
            return;
        }

        final OfflinePlayer offlineMinecraftPlayer = Bukkit.getOfflinePlayer(minecraftUserIdentifier);
        final Role desiredRole = getRoleByLongId(guild, Long.valueOf(this.configStorage.getConfigString("settings.discord-role")));

        if (desiredRole == null) {
            plugin.getLogger().log(Level.WARNING, "Specified role was null, please double check config settings!");
            return;
        }
        guild.getController().addRolesToMember(member, desiredRole).complete();

        channel.sendMessage(
                Replace.replaceString(
                        this.configStorage.getConfigString("messages.user-successfully-linked"),
                        "{minecraft-username}", offlineMinecraftPlayer.getName()
                )
        ).queue();

        if (offlineMinecraftPlayer.isOnline()) {
            final Player onlineMinecraftPlayer = (Player) offlineMinecraftPlayer;
            Message.send(onlineMinecraftPlayer, Replace.replaceString(
                    this.configStorage.getConfigString("messages.user-successfully-linked-mc"),
                    "{discord-username}", member.getUser().getAsTag()
            ));
        }

        this.registeredUserStorage.setLinkedUser(member.getUser().getIdLong(), minecraftUserIdentifier, enteredCode);
        this.registeredUserStorage.saveUser(plugin, new LinkedUser(minecraftUserIdentifier, enteredCode, System.currentTimeMillis()), member.getUser().getIdLong());
    }

    /**
     * Finds and retrieve a role by a given {@link Long} roleID or null
     *
     * @param guild     Our guild instance
     * @param roleId    Our desired role id
     * @return  Returns a {@link Role} matching our id, or null
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
