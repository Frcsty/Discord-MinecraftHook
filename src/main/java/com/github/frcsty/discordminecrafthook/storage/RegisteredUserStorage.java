package com.github.frcsty.discordminecrafthook.storage;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class RegisteredUserStorage {

    @NotNull
    private final Map<String, LinkedUser> storage = new HashMap<>();

    /**
     * Set's a user to our HashMap using entered params,
     * uses {@link System#currentTimeMillis()} as registered date
     *
     * @param memberTag   User member tag
     * @param minecraftID Minecraft UUID from linked user
     * @param usedCode    The code used to link
     */
    public void setLinkedUser(final String memberTag, final UUID minecraftID, final String usedCode) {
        this.storage.put(memberTag, new LinkedUser(minecraftID, usedCode, System.currentTimeMillis()));
    }

    /**
     * Set's a user to out HashMap using entered params
     *
     * @param memberTag   User member tag
     * @param minecraftID Minecraft UUID from linked user
     * @param usedCode    The code used to link
     * @param linkedDate  The date the user linked
     */
    private void setLinkedUser(final String memberTag, final UUID minecraftID, final String usedCode, final long linkedDate) {
        this.storage.put(memberTag, new LinkedUser(minecraftID, usedCode, linkedDate));
    }

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param memberTag User's member tag
     * @return Linked user linked to the member tag
     */
    @Nullable
    public LinkedUser getLinkedUserByMemberTag(final String memberTag) {
        return this.storage.get(memberTag);
    }

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param minecraftID User's Minecraft {@link UUID}
     * @return  Returns a {@link LinkedUser} instance or null
     */
    @Nullable
    public LinkedUser getLinkedUserByMinecraftUUID(final UUID minecraftID) {
        LinkedUser result = null;

        for (final LinkedUser user : this.storage.values()) {
            if (user.getMinecraftIdentifier().equals(minecraftID)) {
                result = user;
                break;
            }
        }

        return result;
    }

    /**
     * Returns a {@link String} member tag belonging to a UUID of a {@link LinkedUser}
     *
     * @param minecraftID   User's Minecraft {@link UUID}
     * @return  Returns a member tag linked to the user
     */
    @Nullable
    public String getLinkedUserMemberTagByUUID(final UUID minecraftID) {
        String result = null;

        for (final String memberTag : this.storage.keySet()) {
            final LinkedUser linkedUser = this.storage.get(memberTag);

            if (linkedUser.getMinecraftIdentifier().equals(minecraftID)) {
                result = memberTag;
                break;
            }
        }
        return result;
    }

    /**
     * Loads RegisteredUser data from our data storage
     *
     * @param plugin Our {@link HookPlugin} instance
     * @throws IOException if the file cannot be created
     */
    public void load(final HookPlugin plugin) throws IOException {
        final File dataFile = new File(plugin.getDataFolder(), "registered-users.yml");

        if (!dataFile.exists()) {
            dataFile.createNewFile();
            return;
        }

        final FileConfiguration dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        final ConfigurationSection usersSection = dataConfiguration.getConfigurationSection("users");
        if (usersSection == null) return;

        for (final String key : usersSection.getKeys(false)) {
            final ConfigurationSection userSection = usersSection.getConfigurationSection(key);
            if (userSection == null) continue;

            final UUID identifier = UUID.fromString(key);
            final String usedCode = userSection.getString("usedCode");
            final long linkedDate = userSection.getLong("linkedDate");

            final String memberTag = userSection.getString("memberTag");

            final LinkedUser linkedUser = new LinkedUser(
                    identifier,
                    usedCode,
                    linkedDate
            );

            this.storage.put(memberTag, linkedUser);
        }
    }

    /**
     * Saves RegisteredUser data into our data storage
     *
     * @param plugin Our {@link HookPlugin} instance
     * @throws IOException if the file cannot be created
     */
    public void save(final HookPlugin plugin) throws IOException {
        final File dataFile = new File(plugin.getDataFolder(), "registered-users.yml");
        if (this.storage.isEmpty()) return;
        if (!dataFile.exists()) dataFile.createNewFile();

        final FileConfiguration dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection usersSection = dataConfiguration.getConfigurationSection("users");
        if (usersSection == null) usersSection = dataConfiguration.createSection("users");

        for (final String memberTag : this.storage.keySet()) {
            final LinkedUser linkedUser = this.storage.get(memberTag);

            final ConfigurationSection userSection = usersSection.createSection(linkedUser.getMinecraftIdentifier().toString());
            userSection.set("memberTag", memberTag);
            userSection.set("usedCode", linkedUser.getUsedCode());
            userSection.set("linkedDate", linkedUser.getLinkedDate());
        }

        dataConfiguration.save(dataFile);
    }
}
