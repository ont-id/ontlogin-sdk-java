package com.github.ontio;

import com.github.ontio.modules.ServerInfo;
import com.github.ontio.modules.VCFilter;

import java.util.Map;

public class SDKConfig {
    private String[] chain;
    private String[] alg;
    private ServerInfo serverInfo;
    private Map<Integer, VCFilter[]> vcFilters;

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

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Map<Integer, VCFilter[]> getVcFilters() {
        return vcFilters;
    }

    public void setVcFilters(Map<Integer, VCFilter[]> vcFilters) {
        this.vcFilters = vcFilters;
    }
}
