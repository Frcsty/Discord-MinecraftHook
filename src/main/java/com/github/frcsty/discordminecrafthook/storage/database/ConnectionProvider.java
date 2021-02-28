package com.github.frcsty.discordminecrafthook.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public final class ConnectionProvider extends DatabaseFactory {

    @NotNull
    private final HikariDataSource dataSource;
    @NotNull
    private final JavaPlugin plugin;

    /**
     * Set's up our variables
     *
     * @param plugin Our {@link JavaPlugin} instance
     */
    public ConnectionProvider(@NotNull final JavaPlugin plugin) {
        this.dataSource = configureDataSource(plugin);
        this.plugin = plugin;
    }

    /**
     * Returns our initialized database connection, or disables the plugin if it
     * could not be retrieved
     *
     * @return Our Database {@link Connection}
     */
    @Override
    @NotNull
    public java.sql.Connection getConnection() {
        java.sql.Connection connection = null;

        try {
            connection = dataSource.getConnection();
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize database connection!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        if (connection == null)
            throw new RuntimeException("Connection was null");

        return connection;
    }

    /**
     * Get's our database name from hikari.properties
     *
     * @return Our database name
     */
    @Override
    public @NotNull String getDatabaseName() {
        final Properties properties = readPropertiesFile(plugin.getDataFolder() + "/hikari.properties");
        return properties.getProperty("dataSource.databaseName");
    }

    /**
     * Set's up our database to ensure the required tables have been created
     */
    public void setupDatabase() {
        final Connection connection = getConnection();

        try {
            connection.prepareStatement(
                    String.format(
                            Statement.SETUP_REGISTERED_USERS_TABLE,
                            getDatabaseName()
                    )
            ).executeUpdate();

            connection.close();
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An exception has occurred while setting up the database!", ex);
        }
    }
}
