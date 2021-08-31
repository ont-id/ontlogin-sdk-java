package com.github.ontio.modules;

public class VCFilter {
    private String type;
    private String[] express;
    private String[] trustRoots;
    private boolean required;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getExpress() {
        return express;
    }

    public void setExpress(String[] express) {
        this.express = express;
    }

    public String[] getTrustRoots() {
        return trustRoots;
    }

    public void setTrustRoots(String[] trustRoots) {
        this.trustRoots = trustRoots;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
