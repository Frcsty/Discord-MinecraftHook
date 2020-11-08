package com.github.frcsty.discordminecrafthook;

import com.github.frcsty.discordminecrafthook.bukkit.command.SyncRankCommand;
import com.github.frcsty.discordminecrafthook.bukkit.command.UnlinkCommand;
import com.github.frcsty.discordminecrafthook.bukkit.command.VerifyCommand;
import com.github.frcsty.discordminecrafthook.data.cache.RequestCache;
import com.github.frcsty.discordminecrafthook.data.registry.DataRegistry;
import com.github.frcsty.discordminecrafthook.data.registry.RegistryHandler;
import com.github.frcsty.discordminecrafthook.discord.DiscordProvider;
import com.github.frcsty.discordminecrafthook.util.Property;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.entities.Guild;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Warning;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class HookPlugin extends JavaPlugin {

    public static String dataFolder;

    @NotNull private final DataRegistry dataRegistry = new DataRegistry(this);

    @NotNull private final RequestCache requestCache = new RequestCache();
    @NotNull private final DiscordProvider discordProvider = new DiscordProvider(this);

    private final Map<Group, Long> roleAssociations = new HashMap<>();

    @Override
    public void onEnable() {
        dataFolder = getDataFolder().getPath();

        saveResources(
                "plugin.properties"
        );

        this.dataRegistry.initialize();
        this.discordProvider.initialize();

        setRankRoleProperties();

        registerCommands(
                new VerifyCommand(this),
                new UnlinkCommand(this),
                new SyncRankCommand(this)
        );
    }

    /**
     * Returns our loaded {@link RegistryHandler}
     *
     * @return A loaded {@link RegistryHandler}
     */
    @NotNull
    public RegistryHandler getRegistryHandler() {
        return this.dataRegistry.getRegistryHandler();
    }

    /**
     * Returns out {@link RequestCache}
     *
     * @return A loaded {@link RequestCache}
     */
    @NotNull
    public RequestCache getRequestCache() {
        return this.requestCache;
    }

    /**
     * Returns access to the {@link LuckPerms} API
     *
     * @return {@link LuckPerms} API
     */
    @NotNull
    public LuckPerms getLuckPermsProvider() {
        return LuckPermsProvider.get();
    }

    /**
     * Returns associated guild
     *
     * @return {@link Guild}
     */
    @Nullable
    public Guild getLinkedDiscordGuild() {
        return this.discordProvider.getLinkedGuild();
    }

    /**
     * Saves resources, without outputting and "already exists" messages into console
     *
     * @param resources Desired resources path's to be saved
     */
    public void saveResources(@NotNull final String... resources) {
        Arrays.stream(resources).forEach(resource -> {
            if (!new File(getDataFolder(), resource).exists()) saveResource(resource, false);
        });
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
     * Loads the Minecraft Groups provided by LuckPerms to the Properties file
     * to be associated with correct Discord Role IDs
     */
    private void setRankRoleProperties() {
        final Properties properties = Property.readPropertiesFile(getDataFolder() + "/plugin.properties");
        final boolean configured = Boolean.valueOf(properties.getProperty("group.loaded-roles"));

        final Set<Group> groups = getLuckPermsProvider().getGroupManager().getLoadedGroups();

        if (!configured) {
            groups.forEach(group -> properties.setProperty(group.getName(), "<set designated role ID>"));

            properties.setProperty("group.loaded-roles", "true");
            Property.savePropertiesFile(getDataFolder() + "/plugin.properties", properties);
            return;
        }

        groups.forEach(group -> roleAssociations.put(group, Long.valueOf(properties.getProperty(group.getName()))));
    }

    public Map<Group, Long> getRoleAssociations() {
        return this.roleAssociations;
    }

}
