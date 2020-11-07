package com.github.frcsty.discordminecrafthook.discord;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.discord.listener.VerifyCommandListener;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public final class DiscordSetup {

    @NotNull private final HookPlugin plugin;
    @NotNull private final ConfigStorage configStorage;

    private JDA jda;
    private Guild linkedGuild;

    public DiscordSetup(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
        this.configStorage = plugin.getConfigStorage();
    }

    /**
     * Initializes the bot and it's listeners
     */
    public void initialize() {
        this.jda = startBot();

        jda.addEventListener(new VerifyCommandListener(plugin));
    }

    /**
     * Constructs a Discord Bot using config token and returns it's instance
     *
     * @return A JDA Instance of the constructed bot
     */
    @NotNull private JDA startBot() {
        JDA jda = null;

        try {
            jda = new JDABuilder().setToken(this.configStorage.getConfigString("settings.bot-token"))
                    .setStatus(OnlineStatus.ONLINE)
                    .build();
        } catch (final LoginException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Discord bot was unable to start! Please verify the bot token is correct.");
            this.plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        if (jda == null) throw new RuntimeException("JDA Provider was null! Failed to proceed.");

        this.linkedGuild = jda.getGuildById(this.configStorage.getConfigString("settings.guild-id"));

        return jda;
    }

    /**
     * Returns our active jda instance
     *
     * @return Active {@link JDA} instance
     */
    @NotNull public JDA getActiveJDAInstance() {
        return this.jda;
    }

    /**
     * Returns our linked guild
     *
     * @return Linked {@link Guild} instance
     */
    @NotNull public Guild getLinkedGuild() {
        return this.linkedGuild;
    }

}
