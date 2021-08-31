package com.github.ontio.modules;

public class ClientHello {
    private String ver;
    private String type;
    private String action;
    private ClientChallenge clientChallenge;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ClientChallenge getClientChallenge() {
        return clientChallenge;
    }

    public void setClientChallenge(ClientChallenge clientChallenge) {
        this.clientChallenge = clientChallenge;
    }
}
