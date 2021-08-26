package com.github.ontio;

import com.github.ontio.modules.ServerInfo;
import com.github.ontio.modules.VCFilter;

import java.util.Map;

public class SDKConfig {
    private String[] chain;
    private String[] alg;
    private ServerInfo serverInfo;
    private Map<String, VCFilter[]> vcFilters;

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

    public Map<String, VCFilter[]> getVcFilters() {
        return vcFilters;
    }

    public void setVcFilters(Map<String, VCFilter[]> vcFilters) {
        this.vcFilters = vcFilters;
    }
}
