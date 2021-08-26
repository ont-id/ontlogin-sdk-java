package com.github.ontio.did.ont;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.did.DidProcessor;
import com.github.ontio.modules.VCFilter;
import com.github.ontio.ontid.OntId2;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;
import com.github.ontio.ontid.jwt.JWTCredential;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OntProcessor implements DidProcessor {
    private OntSdk sdk;
    private boolean isDoubleDirectionVerify;
    private Account account;

    public OntProcessor(boolean doubleDirection, String nodeRestfulUrl, String didContractAddr, String walletFile, String password) throws Exception {
        this.sdk = OntSdk.getInstance();
        this.sdk.setRestful(nodeRestfulUrl);
        this.isDoubleDirectionVerify = doubleDirection;
        if (doubleDirection) {
            if (didContractAddr.length() == 0) {
                throw new SDKException("did contract should not be empty");
            }
            this.sdk.neovm().credentialRecord().setContractAddress(didContractAddr);
            this.sdk.openWalletFile(walletFile);
//            this.sdk.getWalletMgr().getAccount(address,password);
            String address = this.sdk.getWalletMgr().getDefaultAccount().address;
            this.account = this.sdk.getWalletMgr().getAccount(address, password);
        }
    }

    @Override
    public void verifySig(String did, int index, byte[] msg, byte[] sig) throws Exception {
        String publicKey = getDIDPubKey(did, index);
        Account account = new Account(false, Helper.hexToBytes(publicKey));
        boolean verify = account.verifySignature(msg, sig);
        if (!verify) {
            throw new SDKException("verify signature failed");
        }
    }

    private String getDIDPubKey(String did, int index) throws Exception {
        String publicKeys = sdk.nativevm().ontId().sendGetPublicKeys(did);
        JSONArray jsonArray = JSONObject.parseArray(publicKeys);
        JSONObject jsonObject = jsonArray.getJSONObject(index - 1);
        return jsonObject.getString("publicKeyHex");
    }

    @Override
    public byte[] sign(String did, int index, byte[] msg) {
        DataSignature sign = new DataSignature(account.getSignatureScheme(), account, msg);
        return sign.signature();
    }

    @Override
    public void verifyPresentation(String did, int index, String presentation, VCFilter[] requiredTypes) throws Exception {
        OntId2 ontId2 = sdk.neovm().ontId2();
        boolean verifyJWTCredSignature = ontId2.verifyJWTCredSignature(presentation);
        if (!verifyJWTCredSignature) {
            throw new SDKException("verifyJWTCredSignature failed");
        }
        JWTCredential jwtCredential = JWTCredential.deserializeToJWTCred(presentation);
        VerifiablePresentation verifiablePresentation = VerifiablePresentation.deserializeFromJWT(jwtCredential);
        Proof[] proof = verifiablePresentation.proof;
        for (int i = 0; i < proof.length; i++) {
            boolean verifyPresentationProof = ontId2.verifyPresentationProof(verifiablePresentation, i);
            if (!verifyPresentationProof) {
                throw new SDKException("verifyPresentationProof failed,index:" + i);
            }
        }
        VerifiableCredential[] verifiableCredentials = verifiablePresentation.verifiableCredential;
        List<String> credTypes = new ArrayList<>();
        for (int i = 0; i < verifiableCredentials.length; i++) {
            VerifiableCredential vc = verifiableCredentials[i];
            JWTCredential credential = new JWTCredential(vc);
            String cred = credential.toString();
            String[] type = vc.type;
            String[] trustRoot = Util.getTrustRoot(type, requiredTypes);
            verifyCredential(did, index, cred, trustRoot);
            credTypes.addAll(Arrays.asList(type));
        }
        if (requiredTypes != null) {
            for (VCFilter required : requiredTypes) {
                if (!required.isRequired()) {
                    continue;
                }
                boolean flag = false;
                for (String ctype : credTypes) {
                    if (ctype.equals(required.getType())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    throw new SDKException("required credential:+" + required + " is not existed");
                }
            }
        }
    }

    @Override
    public void verifyCredential(String did, int index, String credential, String[] trustedDIDs) throws Exception {
        OntId2 ontId2 = sdk.neovm().ontId2();
        //1. verify signer
        boolean verifyJWTCredSignature = ontId2.verifyJWTCredSignature(credential);
        if (!verifyJWTCredSignature) {
            throw new SDKException("verifyJWTCredSignature failed");
        }
        //2. verify issuance date
        //3. verify expiration date
        boolean verifyJWTCredDate = ontId2.verifyJWTCredDate(credential);
        if (!verifyJWTCredDate) {
            throw new SDKException("verifyJWTCredDate failed");
        }
        //4. verify trusted issuer did
        boolean verifyJWTCredOntIdCredible = ontId2.verifyJWTCredOntIdCredible(trustedDIDs, credential);
        if (!verifyJWTCredOntIdCredible) {
            throw new SDKException("verifyJWTCredOntIdCredible failed");
        }
        //5. verify status
        boolean verifyJWTCredNotRevoked = ontId2.verifyJWTCredNotRevoked(credential);
        if (!verifyJWTCredNotRevoked) {
            throw new SDKException("verifyJWTCredNotRevoked failed");
        }
    }

    @Override
    public String[] getCredentialJsons(String presentation) throws Exception {
        JWTCredential jwtCredential = JWTCredential.deserializeToJWTCred(presentation);
        VerifiablePresentation vp = VerifiablePresentation.deserializeFromJWT(jwtCredential);
        VerifiableCredential[] verifiableCredentials = vp.verifiableCredential;
        String[] creds = new String[verifiableCredentials.length];
        for (int i = 0; i < verifiableCredentials.length; i++) {
            VerifiableCredential vc = verifiableCredentials[i];
            JWTCredential credential = new JWTCredential(vc);
            creds[i] = credential.toString();
        }
        return creds;
    }
}
