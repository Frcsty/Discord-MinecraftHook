package com.github.frcsty.discordminecrafthook.storage.database;

import org.jetbrains.annotations.NotNull;

public final class Statement {

    @NotNull
    public static final String REGISTERED_USERS_TABLE = "registered_users";

    @NotNull
    public static final String REMOVE_PLAYER_DATA = "DELETE FROM `%s`.`%s` WHERE uuid='%s'";
    @NotNull
    public static final String SELECT_USER_FROM_TABLE = "SELECT * FROM `%s`.`%s` WHERE uuid=%s";
    @NotNull
    public static final String SELECT_USER_FROM_TABLE_BY_DISCORD_ID = "SELECT * FROM `%s`.`%s` WHERE memberTag=%s";
    @NotNull
    public static final String UPDATE_PLAYER_DATA = "INSERT INTO `%s`.`%s` (uuid, minecraftUsername, memberTag, usedCode, linkedDate) VALUES ('%s', '%s', '%s', '%s', %s) ON DUPLICATE KEY UPDATE minecraftUsername='%s', memberTag='%s', usedCode='%s', linkedDate=%s;";
    @NotNull
    static final String SETUP_REGISTERED_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `%s`.`" + REGISTERED_USERS_TABLE + "` (`uuid` CHAR(36) NOT NULL, `minecraftUsername` VARCHAR(16) NOT NULL, `memberTag` VARCHAR(36) NOT NULL, `usedCode` VARCHAR(30) NOT NULL, `linkedDate` LONG, PRIMARY KEY (`uuid`));";

}
