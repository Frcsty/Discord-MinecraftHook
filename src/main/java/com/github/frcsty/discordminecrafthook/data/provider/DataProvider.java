package com.github.frcsty.discordminecrafthook.data.provider;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import com.github.frcsty.discordminecrafthook.util.Property;
import org.jetbrains.annotations.NotNull;

/**
 * Provides us with the configured provider based on {@link com.github.frcsty.discordminecrafthook.data.provider.AbstractDataProvider}
 * Allows for multiple storage implementations such as MYSQL or File storage for our data registry, and cache
 */
public final class DataProvider {

    @NotNull private final HookPlugin plugin;

    private AbstractDataProvider abstractDataProvider;

    public DataProvider(@NotNull final HookPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        this.abstractDataProvider = returnProviderFromProperty();
    }

    /**
     * Returns the {@link AbstractDataProvider} subclass of the configured data provider
     *
     * @return A subclass extending {@link AbstractDataProvider} of the configured provider from the properties file
     */
    @NotNull
    public AbstractDataProvider getInitializedDataProvider() {
        return this.abstractDataProvider;
    }

    /**
     * Retrieves and initializes a subclass of {@link AbstractDataProvider} depending
     * on the configured type from the properties file
     */
    @NotNull
    private AbstractDataProvider returnProviderFromProperty() {
        final String providerType = Property.getByKey("settings.provider-type");

        switch (providerType.toUpperCase()) {
            case "SQL":
                return new DatabaseProvider(plugin);
            case "FILE":
                return new FileProvider(plugin);
        }

        throw new RuntimeException("A storage provider could not be retained from the given properties type!");
    }

}
