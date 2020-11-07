package com.github.frcsty.discordminecrafthook.data.provider;

import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbstractDataProvider {

    @Nullable RegistryUser loadUser(@NotNull final Object userIdentifier);

    void saveUser(@NotNull final RegistryUser user);

    void removeUser(@NotNull final RegistryUser user);

}
