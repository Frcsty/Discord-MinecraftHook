package com.github.frcsty.discordminecrafthook.util;

import com.github.frcsty.discordminecrafthook.HookPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public final class Task {

    private static final HookPlugin PLUGIN = JavaPlugin.getPlugin(HookPlugin.class);

    public static void async(final Runnable runnable) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(PLUGIN, runnable);
    }

    public static void queue(final Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, runnable);
    }

}
