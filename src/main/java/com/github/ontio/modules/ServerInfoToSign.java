package com.github.ontio.modules;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders={"name","url","did"})
public class ServerInfoToSign {
    private String name;
    private String url;
    private String did;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
