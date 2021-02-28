package com.github.frcsty.discordminecrafthook.storage;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.storage.database.ConnectionProvider;
import com.github.frcsty.discordminecrafthook.storage.database.Statement;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import com.github.frcsty.discordminecrafthook.util.Task;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RegisteredUserStorage {

    private ConnectionProvider connectionProvider;

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param memberID User's member tag
     * @return Linked user linked to the member tag
     */
    @Nullable
    public LinkedUser getLinkedUserByMemberTag(final long memberID) {
        return getLinkedUser(memberID);
    }

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param minecraftID User's Minecraft {@link UUID}
     * @return Returns a {@link LinkedUser} instance or null
     */
    @Nullable
    public LinkedUser getLinkedUserByMinecraftUUID(final UUID minecraftID) {
        return getLinkedUser(minecraftID);
    }

    /**
     * Returns a {@link String} member tag belonging to a UUID of a {@link LinkedUser}
     *
     * @param minecraftID User's Minecraft {@link UUID}
     * @return Returns a member tag linked to the user
     */
    public long getLinkedUserMemberTagByUUID(final UUID minecraftID) {
        final LinkedUser user = getLinkedUser(minecraftID);
        if (user == null)
            return 0;

        return user.getDiscordIdentifier();
    }

    /**
     * Loads RegisteredUser data from our data storage
     *
     * @param plugin Our {@link HookPlugin} instance
     */
    public void load(final HookPlugin plugin) {
        final Logger logger = plugin.getLogger();

        try {
            this.connectionProvider = new ConnectionProvider(plugin);

            this.connectionProvider.setupDatabase();
        } catch (final Exception ex) {
            logger.log(Level.WARNING, "Failed to initialize the plugin!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public LinkedUser getLinkedUser(final UUID identifier) {
        final Connection connection = this.connectionProvider.getConnection();
        final String databaseName = this.connectionProvider.getDatabaseName();

        try {
            final ResultSet result = connection.prepareStatement(
                    String.format(
                            Statement.SELECT_USER_FROM_TABLE,
                            databaseName, Statement.REGISTERED_USERS_TABLE, identifier.toString()
                    )
            ).executeQuery();

            if (result != null) {
                final UUID minecraftID = UUID.fromString(result.getString("uuid"));
                final String minecraftUsername = result.getString("minecraftUsername");
                final long memberID = result.getLong("memberTag");
                final String usedCode = result.getString("usedCode");
                final long linkedDate = result.getLong("linkedDate");

                result.close();
                return new LinkedUser(
                        minecraftID,
                        minecraftUsername,
                        usedCode,
                        linkedDate,
                        memberID
                );
            }

            connection.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public LinkedUser getLinkedUser(final long identifier) {
        final Connection connection = this.connectionProvider.getConnection();
        final String databaseName = this.connectionProvider.getDatabaseName();

        try {
            final ResultSet result = connection.prepareStatement(
                    String.format(
                            Statement.SELECT_USER_FROM_TABLE_BY_DISCORD_ID,
                            databaseName, Statement.REGISTERED_USERS_TABLE, identifier
                    )
            ).executeQuery();

            if (result != null) {
                final UUID minecraftID = UUID.fromString(result.getString("uuid"));
                final String minecraftUsername = result.getString("minecraftUsername");
                final long memberID = result.getLong("memberTag");
                final String usedCode = result.getString("usedCode");
                final long linkedDate = result.getLong("linkedDate");

                result.close();
                return new LinkedUser(
                        minecraftID,
                        minecraftUsername,
                        usedCode,
                        linkedDate,
                        memberID
                );
            }

            connection.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Saves the provided {@link LinkedUser} to the database
     *
     * @param plugin     Our {@link HookPlugin} instance
     * @param linkedUser Linked user we wish to save the data for
     * @param memberID   Linked user Discord Identifier
     */
    public void saveUser(final HookPlugin plugin, final LinkedUser linkedUser, final long memberID) {
        final Connection connection = this.connectionProvider.getConnection();

        Task.async(() -> {
            try {
                connection.prepareStatement(
                        String.format(
                                Statement.UPDATE_PLAYER_DATA,
                                this.connectionProvider.getDatabaseName(), Statement.REGISTERED_USERS_TABLE,
                                linkedUser.getMinecraftIdentifier(), linkedUser.getMinecraftUsername(), memberID, linkedUser.getUsedCode(), linkedUser.getLinkedDate(),
                                linkedUser.getMinecraftUsername(), memberID, linkedUser.getUsedCode(), linkedUser.getLinkedDate()
                        )
                ).executeUpdate();

                connection.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.WARNING, "An exception occurred while saving data for user '" + linkedUser.getMinecraftIdentifier() + "'!", ex);
            }
        });
    }

    /**
     * @param plugin     Our {@link HookPlugin} instance
     * @param linkedUser Linked user to be removed
     */
    public void invalidateUser(final HookPlugin plugin, final LinkedUser linkedUser) {
        Task.async(() -> {
            try {
                final Connection connection = this.connectionProvider.getConnection();

                connection.prepareStatement(
                        String.format(
                                Statement.REMOVE_PLAYER_DATA,
                                this.connectionProvider.getDatabaseName(), Statement.REGISTERED_USERS_TABLE,
                                linkedUser.getMinecraftIdentifier()
                        )
                ).executeUpdate();

                connection.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.WARNING, "An exception occurred while removing data for user '" + linkedUser.getMinecraftIdentifier() + "'!", ex);
            }
        });
    }

}
