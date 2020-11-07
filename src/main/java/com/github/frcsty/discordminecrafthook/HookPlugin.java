package com.github.frcsty.discordminecrafthook;

import com.github.frcsty.discordminecrafthook.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.command.UnlinkCommand;
import com.github.frcsty.discordminecrafthook.command.VerifyCommand;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.discord.DiscordSetup;
import com.github.frcsty.discordminecrafthook.storage.MinecraftGroupProvider;
import com.github.frcsty.discordminecrafthook.storage.RegisteredUserStorage;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class HookPlugin extends JavaPlugin {

    @NotNull private final ConfigStorage configStorage = new ConfigStorage();
    @NotNull private final RequestCache cache = new RequestCache(configStorage);
    @NotNull private final DiscordSetup discordSetup = new DiscordSetup(this);

    @NotNull private final RegisteredUserStorage registeredUserStorage = new RegisteredUserStorage();
    @NotNull private final MinecraftGroupProvider minecraftGroupProvider = new MinecraftGroupProvider(this);

    private LuckPerms luckPerms;

    @Override public void onEnable() {
        saveDefaultConfig();

        saveResources(
                "hikari.properties"
        );

        CompletableFuture.supplyAsync(() -> {
            this.configStorage.load(this);
            this.registeredUserStorage.load(this);
            this.minecraftGroupProvider.setupConfig();

            registerCommands(
                    new VerifyCommand(this),
                    new UnlinkCommand(this)
            );

            this.discordSetup.initialize();
            //new UserListener(this);

            this.minecraftGroupProvider.initializeRunnable();

            return null;
        }).exceptionally(ex -> {
            getLogger().log(Level.SEVERE, "An exception has occurred while initializing the plugin!", ex);
            return null;
        });
    }

    @Override public void onDisable() {
        reloadConfig();

        this.registeredUserStorage.save(this);
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
     * Returns our Registered User Storage
     *
     * @return A loaded instance of {@link RegisteredUserStorage}
     */
    @NotNull public RegisteredUserStorage getRegisteredUserStorage() {
        return this.registeredUserStorage;
    }

    /**
     * Returns a loaded luck perms provider
     *
     * @return The loaded {@link LuckPerms} provider
     */
    @NotNull public LuckPerms getLuckPermsProvider() {
        return this.luckPerms;
    }

    /**
     * Returns our Minecraft Group Provider
     *
     * @return A loaded instance of {@link MinecraftGroupProvider}
     */
    @NotNull public MinecraftGroupProvider getMinecraftGroupProvider() {
        return this.minecraftGroupProvider;
    }

    /**
     * Returns our Discord Setup instance
     *
     * @return our active {@link DiscordSetup} instance
     */
    @NotNull public DiscordSetup getDiscordProvider() {
        return this.discordSetup;
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

    /**
     * Saves resources, without outputting and "already exists" messages into console
     *
     * @param resources Desired resources path's to be saved
     */
    private void saveResources(final String... resources) {
        Arrays.stream(resources).forEach(resource -> {
            if (!new File(getDataFolder(), resource).exists()) saveResource(resource, false);
        });
    }

    /**
     * Retrieves and initializes our {@link LuckPerms} API provider,
     * disables this {@link HookPlugin} if the provider could not be retrieved
     */
    private void retrieveLuckPermsProvider() {
        final RegisteredServiceProvider<LuckPerms> luckPermsProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (luckPermsProvider == null) {
            getLogger().log(Level.SEVERE, "Failed to find LuckPerms.class Provider! Disabling plugin.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        this.luckPerms = luckPermsProvider.getProvider();
    }

}
