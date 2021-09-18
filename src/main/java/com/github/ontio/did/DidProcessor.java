package com.github.ontio.did;

import com.github.ontio.modules.VCFilter;

public interface DidProcessor {
    void verifySig(String did, int index, byte[] msg, byte[] sig) throws Exception;

    byte[] sign(String did, int index, byte[] msg);

    void verifyPresentation(String presentation, VCFilter[] requiredTypes) throws Exception;

    void verifyCredential(String credential, String[] trustedDIDs) throws Exception;

    String[] getCredentialJsons(String presentation) throws Exception;
}
