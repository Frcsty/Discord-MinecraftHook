package com.github.frcsty.discordminecrafthook.data.provider.database;

import org.jetbrains.annotations.NotNull;

public final class Statement {

    @NotNull public static final String REGISTERED_USERS_TABLE = "registered_users";

    @NotNull public static final String REMOVE_PLAYER_DATA = "DELETE FROM `%s`.`%s` WHERE uuid='%s'";
    @NotNull public static final String SELECT_USER_FROM_TABLE = "SELECT * FROM `%s`.`%s` where uuid='%s'";
    @NotNull public static final String UPDATE_PLAYER_DATA = "INSERT INTO `%s`.`%s` (uuid, minecraftUsername, memberTag, memberID, verifyCodeUsed, verifyDate) VALUES ('%s', '%s', '%s', '%s', '%s', %s) ON DUPLICATE KEY UPDATE minecraftUsername='%s', memberTag='%s', memberID='%s' verifyCodeUsed='%s', verifyDate=%s;";

    @NotNull static final String SETUP_DATABASE = "CREATE DATABASE IF NOT EXISTS `%s`;";
    @NotNull static final String SETUP_REGISTERED_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `%s`.`" + REGISTERED_USERS_TABLE + "` (`uuid` CHAR(36) NOT NULL, `minecraftUsername` VARCHAR(16) NOT NULL, `memberID` LONG NOT NULL, `memberTag` VARCHAR(36) NOT NULL, `verifyCodeUsed` VARCHAR(30) NOT NULL, `verifyDate` LONG, PRIMARY KEY (`uuid`));";

}
