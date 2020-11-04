package com.github.frcsty.discordminecrafthook.storage.database;

import org.jetbrains.annotations.NotNull;

public final class Statement {

    @NotNull public static final String REGISTERED_USERS_TABLE = "registered_users";

    @NotNull public static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM `%s`.`%s`";
    @NotNull public static final String UPDATE_PLAYER_DATA = "INSERT INTO `%s`.`%s` (uuid, minecraftUsername, memberTag, codeUsed, linkedDate) VALUES (`%s`, `%s`, `%s`, `%s`, %s);";
    @NotNull static final String SETUP_DATABASE = "CREATE DATABASE IF NOT EXISTS `%s`;";
    @NotNull static final String SETUP_REGISTERED_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `%s`." + REGISTERED_USERS_TABLE + "` (`uuid` CHAR(36) NOT NULL, `minecraftUsername` VARCHAR(16) NOT NULL, `memberTag` VARCHAR(36) NOT NULL, `usedCode` VARCHAR(30) NOT NULL, `linkedDate` NULL, PRIMARY KEY (`uuid`));";

}
