package com.github.frcsty.discordminecrafthook.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public abstract class DatabaseFactory {

    @NotNull
    public abstract Connection getConnection();

    @NotNull
    public abstract String getDatabaseName();

    /**
     * Constructs a hikari data source from our file
     *
     * @param plugin Our {@link JavaPlugin} instance
     * @return A {@link HikariDataSource} constructed from our hikari.properties file
     */
    @NotNull HikariDataSource configureDataSource(@NotNull final JavaPlugin plugin) {
        final HikariConfig config = new HikariConfig(
                plugin.getDataFolder() + "/hikari.properties"
        );

        return new HikariDataSource(config);
    }

    /**
     * Returns a Property from a specified file name
     *
     * @param fileName Desired file name
     * @return A Property of our specified file
     */
    @NotNull Properties readPropertiesFile(@NotNull final String fileName) {
        FileInputStream inputStream = null;
        final Properties properties = new Properties();

        try {
            try {
                inputStream = new FileInputStream(fileName);
                properties.load(inputStream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        return properties;
    }

}