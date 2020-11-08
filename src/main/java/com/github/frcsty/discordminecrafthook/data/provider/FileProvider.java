package com.github.frcsty.discordminecrafthook.data.provider;

import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
final class FileProvider implements AbstractDataProvider {

    @NotNull private final JavaPlugin plugin;

    FileProvider(@NotNull final JavaPlugin plugin) {
        this.plugin = plugin;

        final File directory = new File(plugin.getDataFolder() + "/user-data");
        if (!directory.exists()) directory.mkdir();
    }

    /**
     * Loads and returns a {@link RegistryUser} or null if the user does not exist
     *
     * @param userIdentifier    The specific user identifier, either their UUID, or Discord member ID
     * @return  Returns a {@link RegistryUser} associated to the identifier, or null if invalid
     */
    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public RegistryUser loadUser(@NotNull final UUID userIdentifier) {
        final File userFile = getFileAssociated(userIdentifier);

        if (userFile != null) {
            final FileConfiguration configuration = YamlConfiguration.loadConfiguration(userFile);
            final ConfigurationSection dataSection = configuration.getConfigurationSection("data");

            if (dataSection == null) return null;
            final RegistryUser user = new RegistryUser(
                    dataSection.getString("memberTag"),
                    dataSection.getLong("memberID"),
                    UUID.fromString(dataSection.getString("minecraftUUID")),
                    dataSection.getString("minecraftUsername")
            );

            user.setUserAsLinked(dataSection.getString("verifyCodeUsed"), dataSection.getLong("verifyDate"));

            return user;
        }

        return null;
    }

    /**
     * Saves the given user to file
     *
     * @param user  Desired {@link RegistryUser} to be saved
     */
    @Override
    public void saveUser(@NotNull final RegistryUser user) {
        final File userFile = new File(plugin.getDataFolder() + "/user-data", getFileName(user));
        final FileConfiguration configuration = YamlConfiguration.loadConfiguration(userFile);

        final ConfigurationSection dataSection = configuration.createSection("data");
        dataSection.set("memberTag", user.getMemberTag());
        dataSection.set("memberID", user.getMemberID());
        dataSection.set("verifyCodeUsed", user.getVerifyCodeUsed());
        dataSection.set("verifyDate", user.getVerifyDate());

        dataSection.set("minecraftUUID", user.getMinecraftUUID().toString());
        dataSection.set("minecraftUsername", user.getMinecraftUsername());

        try {
            configuration.save(userFile);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes a {@link RegistryUser} from storage
     *
     * @param user  The {@link RegistryUser} to be removed
     */
    @Override
    public void removeUser(@NotNull final RegistryUser user) {
        final File userFile = getFileAssociated(user.getMemberID());

        if (userFile == null) {
            return;
        }

        userFile.delete();
    }

    /**
     * Returns a file name formatted as "<UUID>.yml" from the given user
     *
     * @param user  Specified {@link RegistryUser}
     * @return  File name {@link String} generated from user details
     */
    @NotNull private String getFileName(@NotNull final RegistryUser user) {
        return user.getMinecraftUUID().toString() + ".yml";
    }

    /**
     * Returns a {@link File} associated to the given user,
     * or null if none can be found
     *
     * @param identifier User identifier
     * @return  {@link File} associated to the given user or null
     */
    @Nullable
    private File getFileAssociated(final Object identifier) {
        final File directory = new File(plugin.getDataFolder() + "/user-data/");

        if (!directory.exists()) directory.mkdir();
        final File[] files = directory.listFiles();

        if (files == null || files.length == 0) return null;
        File result = null;

        for (final File file : files) {
            final String fileName = file.getName();

            if (fileName.contains(identifier.toString())) {
                result = file;
                break;
            }
        }

        return result;
    }

}
