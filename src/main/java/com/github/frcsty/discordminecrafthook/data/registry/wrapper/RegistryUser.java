package com.github.frcsty.discordminecrafthook.data.registry.wrapper;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Holds all relative data for our user
 */
public final class RegistryUser {

    // Discord Sided
    private final String memberTag;
    private final long memberID;

    private String verifyCodeUsed;
    private long verifyDate;

    // Minecraft Sided
    private final UUID minecraftUUID;
    private final String minecraftUsername;

    public RegistryUser(final String memberTag, final long memberID, final UUID minecraftUUID, final String minecraftUsername) {
        this.memberTag = memberTag;
        this.memberID = memberID;
        this.minecraftUUID = minecraftUUID;
        this.minecraftUsername = minecraftUsername;
    }

    /**
     * Set's the user as verified, while assigning the following parameters accordingly
     *
     * @param codeUsed  The {@link String} code used to verify
     * @param date      The date the user verified
     */
    public void setUserAsLinked(final String codeUsed, final long date) {
        this.verifyCodeUsed = codeUsed;
        this.verifyDate = date;
    }

    /**
     * Returns the users Discord Tag (ie. Frosty#3308)
     *
     * @return  User's Discord Tag
     */
    @NotNull
    public String getMemberTag() {
        return this.memberTag;
    }

    /**
     * Returns the users Discord ID (ie. 307160296714403851)
     *
     * @return  User's Discord Identifier
     */
    public long getMemberID() {
        return this.memberID;
    }

    /**
     * Returns the code used in the verification process
     *
     * @return  The Code Used when the user verified
     */
    @NotNull
    public String getVerifyCodeUsed() {
        return this.verifyCodeUsed;
    }

    /**
     * Returns the date the user verified on
     *
     * @return  The date the user verified on
     */
    public long getVerifyDate() {
        return this.verifyDate;
    }

    /**
     * Returns the users Minecraft Username (ie. Frcsty)
     *
     * @return  Minecraft Player's Username
     */
    @NotNull
    public String getMinecraftUsername() {
        return this.minecraftUsername;
    }

    /**
     * Returns the users Minecraft UUID (ie. a1b72b20-97e0-476e-8a78-db9c9dcf93a4)
     *
     * @return  Minecraft Player's Unique Identifier
     */
    @NotNull
    public UUID getMinecraftUUID() {
        return this.minecraftUUID;
    }
}
