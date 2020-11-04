package com.github.frcsty.discordminecrafthook.storage.wrapper;

import org.bukkit.Bukkit;

import java.util.UUID;

public final class LinkedUser {

    private final UUID minecraftIdentifier;
    private final String minecraftUsername;
    private final String usedCode;
    private final long linkedDate;

    public LinkedUser(final UUID minecraftIdentifier, final String minecraftUsername, final String usedCode, final long linkedDate) {
        this.minecraftIdentifier = minecraftIdentifier;
        this.minecraftUsername = minecraftUsername;
        this.usedCode = usedCode;
        this.linkedDate = linkedDate;
    }

    public LinkedUser(final UUID minecraftIdentifier, final String usedCode, final long linkedDate) {
        this.minecraftIdentifier = minecraftIdentifier;
        this.minecraftUsername = Bukkit.getOfflinePlayer(minecraftIdentifier).getName();
        this.usedCode = usedCode;
        this.linkedDate = linkedDate;
    }

    public String getUsedCode() {
        return this.usedCode;
    }

    public long getLinkedDate() {
        return this.linkedDate;
    }

    public UUID getMinecraftIdentifier() {
        return this.minecraftIdentifier;
    }

    public String getMinecraftUsername() {
        return this.minecraftUsername;
    }

}
