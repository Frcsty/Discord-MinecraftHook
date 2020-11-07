package com.github.frcsty.discordminecrafthook.discord.listener;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.bukkit.command.SyncRankCommand;
import com.github.frcsty.discordminecrafthook.data.registry.RegistryHandler;
import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Property;
import com.github.frcsty.discordminecrafthook.util.Replace;
import com.github.frcsty.discordminecrafthook.util.Role;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

public final class VerifyCommandListener extends ListenerAdapter {

    @NotNull private final HookPlugin plugin;
    @NotNull private final RegistryHandler registryHandler;

    public VerifyCommandListener(@NotNull final HookPlugin plugin) {
        this.registryHandler = plugin.getRegistryHandler();
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();
        final Guild guild = event.getGuild();
        final String content = event.getMessage().getContentRaw();
        if (!content.startsWith("-verify") && !content.startsWith(guild.getSelfMember().getAsMention())) {
            return;
        }

        final User author = event.getAuthor();
        if (author.isBot()) return;

        final Member member = event.getMember();
        if (this.registryHandler.getRegistryUser(member.getUser().getIdLong()) != null) {
            channel.sendMessage(
                    Property.getByKey("message.already-linked")
            ).queue();
            return;
        }

        final String[] arguments = content.split(" ");

        if (arguments.length < 2) {
            channel.sendMessage(
                    Property.getByKey("message.invalid-params")
            ).queue();
            return;
        }

        final String enteredCode = arguments[1];
        final UUID minecraftUserIdentifier = this.plugin.getRequestCache().getUUIDAssociatedTo(enteredCode);

        if (minecraftUserIdentifier == null) {
            channel.sendMessage(
                    Property.getByKey("message.invalid-code")
            ).queue();
            return;
        }

        final OfflinePlayer offlineMinecraftPlayer = Bukkit.getOfflinePlayer(minecraftUserIdentifier);
        final net.dv8tion.jda.api.entities.Role desiredRole = com.github.frcsty.discordminecrafthook.util.Role.getRoleByLongId(guild, Long.valueOf(Property.getByKey("settings.discord-sync-role")));

        if (desiredRole == null) {
            plugin.getLogger().log(Level.WARNING, "Specified role was null, please double check config settings!");
            return;
        }
        guild.addRoleToMember(member, desiredRole).complete();

        channel.sendMessage(
                Replace.replaceString(
                        Property.getByKey("message.user-successfully-linked"),
                        "{minecraft-username}", offlineMinecraftPlayer.getName()
                )
        ).queue();

        final User discordUser = member.getUser();
        final RegistryUser user = new RegistryUser(
                discordUser.getAsTag(),
                discordUser.getIdLong(),
                minecraftUserIdentifier,
                offlineMinecraftPlayer.getName()
        );

        if (offlineMinecraftPlayer.isOnline()) {
            final Player onlineMinecraftPlayer = (Player) offlineMinecraftPlayer;
            Message.send(onlineMinecraftPlayer, Replace.replaceString(
                    Property.getByKey("message.user-successfully-linked-mc"),
                    "{discord-username}", member.getUser().getAsTag()
            ));

            final net.luckperms.api.model.user.User luckPermsUser = plugin.getLuckPermsProvider().getUserManager().getUser(onlineMinecraftPlayer.getUniqueId());
            SyncRankCommand.executeRoleCheck(plugin, onlineMinecraftPlayer, luckPermsUser, user);
        }

        user.setUserAsLinked(enteredCode, System.currentTimeMillis());

        this.registryHandler.saveUser(user);
    }

}
