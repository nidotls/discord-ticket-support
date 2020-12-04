/*
 * Copyright (c) 2020 thenilsdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 */

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
