package com.github.ontio.modules;

public class ClientResponse {
    private String ver;
    private String type;
    private String did;
    private String nonce;
    private Proof proof;
    private String[] vps;

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

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Proof getProof() {
        return proof;
    }

    public void setProof(Proof proof) {
        this.proof = proof;
    }

    public String[] getVps() {
        return vps;
    }

    public void setVps(String[] vps) {
        this.vps = vps;
    }
}
