package com.github.frcsty.discordminecrafthook.data.provider;

import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Set;

final class DatabaseProvider implements AbstractDataProvider {

    @NotNull private final JavaPlugin plugin;

    DatabaseProvider(@NotNull final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public RegistryUser loadUser(@NotNull final Object userIdentifier) {
        return null;
    }

    @Override
    public void saveUser(@NotNull final RegistryUser user) {

    }

    @Override
    public void removeUser(@NotNull RegistryUser user) {

    }
}
