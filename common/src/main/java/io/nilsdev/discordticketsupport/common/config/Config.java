package io.nilsdev.discordticketsupport.common.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Config {

    private final String databaseHost;
    private final int databasePort;
    private final String databaseUser;
    private final String databasePassword;
    private final String databaseName;

    public Config(String databaseHost, int databasePort, String databaseUser, String databasePassword, String databaseName) {
        this.databaseHost = databaseHost;
        this.databasePort = databasePort;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.databaseName = databaseName;
    }
}
