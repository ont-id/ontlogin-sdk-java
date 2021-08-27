package com.github.ontio.modules;

public class ServerHello {
    private String ver;
    private String type;
    private String nonce;
    private ServerInfo server;
    private String[] chain;
    private String[] alg;
    private VCFilter[] VCFilters;
    private ServerProof serverProof;
    private Extension extension;

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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public String[] getChain() {
        return chain;
    }

    public void setChain(String[] chain) {
        this.chain = chain;
    }

    public String[] getAlg() {
        return alg;
    }

    public void setAlg(String[] alg) {
        this.alg = alg;
    }

    public VCFilter[] getVCFilters() {
        return VCFilters;
    }

    public void setVCFilters(VCFilter[] VCFilters) {
        this.VCFilters = VCFilters;
    }

    public ServerProof getServerProof() {
        return serverProof;
    }

    public void setServerProof(ServerProof serverProof) {
        this.serverProof = serverProof;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
