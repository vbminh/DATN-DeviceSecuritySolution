package module;

import java.io.Serializable;

public class InformationDevice implements Serializable {
    private String policy;
    private String serialNo;
    private String token;

    public InformationDevice(String policy, String serialNo, String token) {
        this.policy = policy;
        this.serialNo = serialNo;
        this.token = token;
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

