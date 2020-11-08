package com.github.frcsty.discordminecrafthook.data.provider.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

public abstract class DatabaseFactory {

    @Nullable public abstract Connection getConnection();

    @NotNull public abstract String getDatabaseName();

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

}
