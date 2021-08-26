package com.github.ontio.modules;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders={"type","server","nonce","did","created"})
public class ClientResponseMsg {
    private String type;
    private ServerInfoToSign server;
    private String nonce;
    private String did;
    private String created;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ServerInfoToSign getServer() {
        return server;
    }

    public void setServer(ServerInfoToSign server) {
        this.server = server;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
