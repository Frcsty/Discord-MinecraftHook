package com.github.frcsty.discordminecrafthook.data.registry;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.data.provider.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Exposes access to a {@link RegistryHandler} which handles all given data accordingly
 */
public final class DataRegistry {

    @NotNull private final DataProvider dataProvider;
    @NotNull private final RegistryHandler registryHandler;

    public DataRegistry(@NotNull final HookPlugin plugin) {
        this.dataProvider = new DataProvider(plugin);
        this.registryHandler = new RegistryHandler(dataProvider);
    }

    /**
     * Initializes the {@link DataProvider}
     */
    public void initialize() {
        this.dataProvider.initialize();
    }

    /**
     * Returns our loaded {@link RegistryHandler} which handles our
     * registryUsers {@link Set}
     */
    @NotNull
    public RegistryHandler getRegistryHandler() {
        return this.registryHandler;
    }

}
