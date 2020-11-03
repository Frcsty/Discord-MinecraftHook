package com.github.frcsty.discordminecrafthook.discord.listener;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.util.Replace;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VerifyCommandListener extends ListenerAdapter {

    @NotNull private final ConfigStorage configStorage;
    @NotNull private final RequestCache requestCache;

    public VerifyCommandListener(@NotNull final HookPlugin plugin) {
        this.configStorage = plugin.getConfigStorage();
        this.requestCache = plugin.getRequestCache();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();
        final Guild guild = event.getGuild();
        final String content = event.getMessage().getContentRaw();
        if (!content.startsWith("-verify") && !content.startsWith(guild.getSelfMember().getAsMention())) return;

        final User author = event.getAuthor();
        if (author.isBot()) return;
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
        final Member member = (Member) author;
        member.getRoles().add(guild.getRoleById(this.configStorage.getConfigString("settings.discord-role")));

        channel.sendMessage(
                Replace.replaceString(
                        this.configStorage.getConfigString("messages.user-successfully-linked"),
                        "{minecraft-username}", offlineMinecraftPlayer.getName()
                )
        ).queue();
        // Add to storage for tracking
    }
}
