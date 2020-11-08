package com.github.frcsty.discordminecrafthook.data.provider;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.data.provider.database.ConnectionProvider;
import com.github.frcsty.discordminecrafthook.data.provider.database.Statement;
import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class DatabaseProvider implements AbstractDataProvider {

    @NotNull private final ConnectionProvider connectionProvider;

    DatabaseProvider(@NotNull final HookPlugin plugin) {
        plugin.saveResources(
                "hikari.properties"
        );

        this.connectionProvider = new ConnectionProvider(plugin);
        this.connectionProvider.setupDatabase();
    }

    @Nullable
    @Override
    public RegistryUser loadUser(@NotNull final UUID userIdentifier) {
        final Connection connection = connectionProvider.getConnection();

        if (connection == null) return null;

        try {
            final ResultSet resultSet = connection.prepareStatement(
                    String.format(
                            Statement.SELECT_USER_FROM_TABLE,
                            connectionProvider.getDatabaseName(), Statement.REGISTERED_USERS_TABLE,
                            userIdentifier.toString()
                    )
            ).executeQuery();

            final RegistryUser user = new RegistryUser(
                    resultSet.getString("memberTag"),
                    resultSet.getLong("memberID"),
                    UUID.fromString(resultSet.getString("uuid")),
                    resultSet.getString("minecraftUsername")
            );

            user.setUserAsLinked(resultSet.getString("verifyCodeUsed"), resultSet.getLong("verifyDate"));

            connection.close();
            return user;
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveUser(@NotNull final RegistryUser user) {
        CompletableFuture.supplyAsync(() -> {
            final Connection connection = connectionProvider.getConnection();

            if (connection == null) return null;

            try {
                connection.prepareStatement(
                        String.format(
                                Statement.UPDATE_PLAYER_DATA,
                                connectionProvider.getDatabaseName(), Statement.REGISTERED_USERS_TABLE,
                                user.getMinecraftUUID().toString(), user.getMinecraftUsername(), user.getMemberTag(), user.getMemberID(), user.getVerifyCodeUsed(), user.getVerifyDate(),
                                user.getMinecraftUsername(), user.getMemberTag(), user.getMemberID(), user.getVerifyCodeUsed(), user.getVerifyDate()
                        )
                ).executeUpdate();

                connection.close();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }

            return null;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void removeUser(@NotNull final RegistryUser user) {
        CompletableFuture.supplyAsync(() -> {
            final Connection connection = connectionProvider.getConnection();

            if (connection == null) return null;

            try {
                connection.prepareStatement(
                        String.format(
                                Statement.REMOVE_PLAYER_DATA,
                                connectionProvider.getDatabaseName(), Statement.REGISTERED_USERS_TABLE,
                                user.getMinecraftUUID().toString()
                        )
                ).executeUpdate();

                connection.close();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }

            return null;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
