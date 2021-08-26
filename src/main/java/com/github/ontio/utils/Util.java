package com.github.ontio.utils;

import com.github.ontio.modules.VCFilter;

public class Util {

    public static String[] getTrustRoot(String[] vcType, VCFilter[] filters) {
        for (VCFilter filter : filters) {
            for (String type : vcType) {
                if (type.equals(filter.getType())) {
                    return filter.getTrustRoots();
                }
            }
        }
        return null;
    }
}
