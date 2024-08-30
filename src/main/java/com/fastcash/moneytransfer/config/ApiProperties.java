package com.fastcash.moneytransfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.api")
public record ApiProperties(
        String version,
        String baseUrl,
        String configPath,
        String authPath,
        String tokenPath,
        String tokenRefreshPath,
        String loginPath,
        String userPath,
        String accountPath,
        String transferPath,
        String passwordPath,
        String passwordForgotPath,
        String passwordResetPath,
        String exchangeRatePath,
        String resetPasswordUrlPath
) {

    // Custom getter methods to concatenate baseUrl and version with other paths
	public String fullConfigPath() {
        return baseUrl + version + configPath;
    }
	
	public String fullAuthPath() {
        return baseUrl + version + authPath;
    }

    public String fullTokenPath() {
        return baseUrl + version + authPath + tokenPath;
    }

    public String fullTokenRefreshPath() {
        return baseUrl + version + authPath + tokenRefreshPath;
    }

    public String fullLoginPath() {
        return baseUrl + version + authPath + loginPath;
    }

    public String fullUserPath() {
        return baseUrl + version + userPath;
    }

    public String fullAccountPath() {
        return baseUrl + version + accountPath;
    }

    public String fullTransferPath() {
        return baseUrl + version + transferPath;
    }

    public String fullPasswordPath() {
        return baseUrl + version + passwordPath;
    }

    public String fullPasswordForgotPath() {
        return baseUrl + version + passwordPath + passwordForgotPath;
    }

    public String fullPasswordResetPath() {
        return baseUrl + version + passwordPath + passwordResetPath;
    }

    public String fullExchangeRatePath() {
        return baseUrl + version + exchangeRatePath;
    }
    
}
