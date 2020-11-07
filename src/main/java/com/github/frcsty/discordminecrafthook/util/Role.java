package com.github.frcsty.discordminecrafthook.util;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Role {

    /**
     * Finds and retrieve a role by a given {@link Long} roleID or null
     *
     * @param guild  Our guild instance
     * @param roleId Our desired role id
     * @return Returns a {@link net.dv8tion.jda.api.entities.Role} matching our id, or null
     */
    public static net.dv8tion.jda.api.entities.Role getRoleByLongId(@NotNull final Guild guild, final long roleId) {
        final List<net.dv8tion.jda.api.entities.Role> roles = guild.getRoles();
        net.dv8tion.jda.api.entities.Role result = null;

        for (final net.dv8tion.jda.api.entities.Role role : roles) {
            if (role.getIdLong() == roleId) {
                result = role;
                break;
            }
        }

        return result;
    }

    /**
     * {@see getRoleByLongId}
     *
     * @param guild Our guild instance
     * @param path  Our desired role id
     * @return Returns a {@link net.dv8tion.jda.api.entities.Role} matching our id, or null
     */
    public static net.dv8tion.jda.api.entities.Role getRoleByPropertyKey(@NotNull final Guild guild, @NotNull final String path) {
        return getRoleByLongId(guild, Long.valueOf(Property.getByKey(path)));
    }



}
