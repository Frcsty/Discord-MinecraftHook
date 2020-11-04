package com.github.frcsty.discordminecrafthook.storage;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.storage.database.ConnectionProvider;
import com.github.frcsty.discordminecrafthook.storage.database.Statement;
import com.github.frcsty.discordminecrafthook.storage.wrapper.LinkedUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RegisteredUserStorage {

    @NotNull private final Map<Long, LinkedUser> storage = new HashMap<>();
    @Nullable private ConnectionProvider connectionProvider;

    /**
     * Set's a user to our HashMap using entered params,
     * uses {@link System#currentTimeMillis()} as registered date
     *
     * @param memberID   User member ID
     * @param minecraftID Minecraft UUID from linked user
     * @param usedCode    The code used to link
     */
    public void setLinkedUser(final long memberID, final UUID minecraftID, final String usedCode) {
        this.storage.put(memberID, new LinkedUser(minecraftID, usedCode, System.currentTimeMillis()));
    }

    /**
     * Set's a user to out HashMap using entered params
     *
     * @param memberID   User member ID
     * @param minecraftID Minecraft UUID from linked user
     * @param usedCode    The code used to link
     * @param linkedDate  The date the user linked
     */
    private void setLinkedUser(final long memberID, final UUID minecraftID, final String usedCode, final long linkedDate) {
        this.storage.put(memberID, new LinkedUser(minecraftID, usedCode, linkedDate));
    }

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param memberID User's member tag
     * @return Linked user linked to the member tag
     */
    @Nullable public LinkedUser getLinkedUserByMemberTag(final long memberID) {
        return this.storage.get(memberID);
    }

    /**
     * Returns a {@link LinkedUser} instance if one exists or null
     *
     * @param minecraftID User's Minecraft {@link UUID}
     * @return Returns a {@link LinkedUser} instance or null
     */
    @Nullable public LinkedUser getLinkedUserByMinecraftUUID(final UUID minecraftID) {
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
     * @param minecraftID User's Minecraft {@link UUID}
     * @return Returns a member tag linked to the user
     */
    public long getLinkedUserMemberTagByUUID(final UUID minecraftID) {
        long result = 0;

        for (final long memberID : this.storage.keySet()) {
            final LinkedUser linkedUser = this.storage.get(memberID);

            if (linkedUser.getMinecraftIdentifier().equals(minecraftID)) {
                result = memberID;
                break;
            }
        }
        return result;
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
            logger.log(Level.WARNING, "Database connection was null, failed to initialize plugin!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            final Connection connection = this.connectionProvider.getConnection();
            final String databaseName = this.connectionProvider.getDatabaseName();

            if (connection == null) {
                logger.log(Level.WARNING, "Database connection was null, failed to initialize data!");
                return null;
            }

            try {
                final ResultSet result = connection.prepareStatement(
                        String.format(
                                Statement.SELECT_ALL_FROM_TABLE,
                                databaseName, Statement.REGISTERED_USERS_TABLE
                        )
                ).executeQuery();

                while (result.next()) {
                    final UUID minecraftID = UUID.fromString(result.getString("uuid"));
                    final String minecraftUsername = result.getString("minecraftUsername");
                    final long memberID = result.getLong("memberTag");
                    final String codeUsed = result.getString("codeUsed");
                    final long linkedDate = result.getLong("linkedDate");

                    final LinkedUser linkedUser = new LinkedUser(
                            minecraftID,
                            minecraftUsername,
                            codeUsed,
                            linkedDate
                    );

                    this.storage.put(memberID, linkedUser);
                }

                connection.close();
            } catch (final SQLException ex) {
                logger.log(Level.WARNING, "", ex);
            }
            return null;
        }).exceptionally(ex -> {
            logger.log(Level.WARNING, "An exception has occurred while initializing data!", ex);
            return null;
        });
    }

    /**
     * Saves RegisteredUser data into our data storage
     *
     * @param plugin Our {@link HookPlugin} instance
     */
    public void save(final HookPlugin plugin) {
        final Logger logger = plugin.getLogger();

        try {
            this.connectionProvider = new ConnectionProvider(plugin);

            this.connectionProvider.setupDatabase();
        } catch (final Exception ex) {
            logger.log(Level.WARNING, "Database connection was null, failed to save user data!");
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            final Connection connection = this.connectionProvider.getConnection();
            final String databaseName = this.connectionProvider.getDatabaseName();

            if (connection == null) {
                logger.log(Level.WARNING, "Database connection was null, failed to save data!");
                return null;
            }

            try {
                for (final long memberID : this.storage.keySet()) {
                    final LinkedUser linkedUser = this.storage.get(memberID);

                    connection.prepareStatement(
                            String.format(
                                    Statement.UPDATE_PLAYER_DATA,
                                    databaseName, Statement.REGISTERED_USERS_TABLE,
                                    linkedUser.getMinecraftIdentifier(), linkedUser.getMinecraftUsername(), memberID, linkedUser.getUsedCode(), linkedUser.getLinkedDate()
                            )
                    ).executeUpdate();
                }

                connection.close();
            } catch (final SQLException ex) {
                logger.log(Level.WARNING, "", ex);
            }
            return null;
        }).exceptionally(ex -> {
            logger.log(Level.WARNING, "An exception has occurred while saving data!", ex);
            return null;
        });
    }
}
