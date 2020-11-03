package com.github.frcsty.discordminecrafthook.storage.wrapper;

import java.util.UUID;

public final class LinkedUser {

    private final UUID minecraftIdentifier;
    private final String usedCode;
    private final long linkedDate;

    public LinkedUser(final UUID minecraftIdentifier, final String usedCode, final long linkedDate) {
        this.minecraftIdentifier = minecraftIdentifier;
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
}
