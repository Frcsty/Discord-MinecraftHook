package com.github.frcsty.discordminecrafthook.util;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class Property {

    /**
     * Returns a String from a given Property Key from plugin.properties
     *
     * @param property {@link String} Property key
     * @return Matching property string
     */
    @NotNull
    public static String getByKey(@NotNull final String property) {
        final Properties properties = readPropertiesFile(HookPlugin.dataFolder + "/plugin.properties");

        return properties.getProperty(property);
    }

    /**
     * Returns a Property from a specified file name
     *
     * @param fileName Desired file name
     * @return A Property of our specified file
     */
    @NotNull
    public static Properties readPropertiesFile(@NotNull final String fileName) {
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

    public static void savePropertiesFile(@NotNull final String path, @NotNull final Properties properties) {
        FileOutputStream outputStream = null;

        try {
            try {
                outputStream = new FileOutputStream(path);
                properties.store(outputStream, null);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
