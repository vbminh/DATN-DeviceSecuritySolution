package model;

import java.io.Serializable;

public class DeviceInformation implements Serializable {
    private String policy;
    private String serialNo;
    private String token;

    private String modelName;

    private long sdk;

    private String version;

    public DeviceInformation(String policy, String serialNo, String token, String modelName, long sdk, String version) {
        this.policy = policy;
        this.serialNo = serialNo;
        this.token = token;
        this.modelName = modelName;
        this.sdk = sdk;
        this.version = version;
    }

    public String getModelName() {
        return modelName;
    }

    public long getSdk() {
        return sdk;
    }

    public String getAndroidVersion() {
        return version;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
