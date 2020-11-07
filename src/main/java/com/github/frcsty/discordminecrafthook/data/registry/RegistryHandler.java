package com.github.frcsty.discordminecrafthook.data.registry;

import com.github.frcsty.discordminecrafthook.data.provider.AbstractDataProvider;
import com.github.frcsty.discordminecrafthook.data.provider.DataProvider;
import com.github.frcsty.discordminecrafthook.data.registry.wrapper.RegistryUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistryHandler {

    @NotNull private final AbstractDataProvider dataProvider;

    RegistryHandler( @NotNull final DataProvider dataProvider) {
        this.dataProvider = dataProvider.getInitializedDataProvider();
    }

    /**
     * Returns a {@link RegistryUser} or null if not present
     *
     * @param identifier    User identifier
     * @return  {@link RegistryUser} instance
     */
    @Nullable
    public RegistryUser getRegistryUser(@NotNull final Object identifier) {
        return this.dataProvider.loadUser(identifier);
    }

    /**
     * Deletes a {@link RegistryUser} from storage
     *
     * @param user  {@link RegistryUser} to be deleted
     */
    public void removeUser(@NotNull final RegistryUser user) {
        this.dataProvider.removeUser(user);
    }

    /**
     * Saves a {@link RegistryUser} to storage
     *
     * @param user  {@link RegistryUser} to be saved
     */
    public void saveUser(@NotNull final RegistryUser user) {
        this.dataProvider.saveUser(user);
    }
}
