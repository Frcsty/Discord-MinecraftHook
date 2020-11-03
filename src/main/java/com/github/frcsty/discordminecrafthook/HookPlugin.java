package com.github.frcsty.discordminecrafthook;

import com.github.frcsty.discordminecrafthook.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.command.VerifyCommand;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.discord.DiscordSetup;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Warning;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class HookPlugin extends JavaPlugin {

    @NotNull private final ConfigStorage configStorage = new ConfigStorage();
    @NotNull private final RequestCache cache = new RequestCache(configStorage);

    @Override public void onEnable() {
        saveDefaultConfig();

        this.configStorage.load(this);

        registerCommands(
                new VerifyCommand(this)
        );

        new DiscordSetup(this);
    }

    @Override public void onDisable() {
        reloadConfig();
    }

    /**
     * Returns our Request Cache
     *
     * @return A loaded instance of {@link RequestCache}
     */
    @NotNull public RequestCache getRequestCache() {
        return this.cache;
    }

    /**
     * Returns our Config Storage
     *
     * @return A loaded instance of {@link ConfigStorage}
     */
    @NotNull public ConfigStorage getConfigStorage() {
        return this.configStorage;
    }

    /**
     * Creates a {@link CommandManager} instance and registers provided commands
     *
     * @param commands Desired commands to be registered, extending {@link CommandBase}
     */
    @Warning(reason = "Only ever call this method once to avoid multiple manager instances!")
    private void registerCommands(final CommandBase... commands) {
        final CommandManager manager = new CommandManager(this);

        manager.register(commands);
    }

}
