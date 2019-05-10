package com.orojinmi.theophilus.ducafev2.Model;

public class Admin {
    private String username;
    private String register_id;
    private String fullname;
    private String codename;
    private String role;
    private int privy;
    private String apiKeyId;
    private String appSecretKey;
    private String accessKeyToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegister_id() {
        return register_id;
    }

    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPrivy() {
        return privy;
    }

    public void setPrivy(int privy) {
        this.privy = privy;
    }

    public String getApiKeyId() {
        return apiKeyId;
    }

    public void setApiKeyId(String apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public String getAccessKeyToken() {
        return accessKeyToken;
    }

    public void setAccessKeyToken(String accessKeyToken) {
        this.accessKeyToken = accessKeyToken;
    }
}
