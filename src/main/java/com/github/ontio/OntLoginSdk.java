package com.github.ontio;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.did.DidProcessor;
import com.github.ontio.modules.*;
import com.github.ontio.sdk.exception.SDKException;

import java.util.HashMap;
import java.util.Map;

/**
 * Ont Sdk
 */
public abstract class OntLoginSdk {
    private SDKConfig sdkConfig;
    private Map<String, DidProcessor> didProcessors;

    public OntLoginSdk(SDKConfig sdkConfig, Map<String, DidProcessor> didProcessors) {
        this.sdkConfig = sdkConfig;
        this.didProcessors = didProcessors;
    }

    public abstract String genRandomNonceFunc(Integer action);

    public abstract Integer getActionByNonce(String nonce);

    public String getDIDChain(String did) throws SDKException {
        String[] tmpArr = did.split(":");
        if (tmpArr.length != 3) {
            throw new SDKException("valid did format");
        }
        return tmpArr[1];
    }

    public ServerHello generateChallenge(ClientHello req) throws SDKException {
        validateClientHello(req);
        int action = req.getAction();
        String uuid = genRandomNonceFunc(action);
        ServerHello serverHello = new ServerHello();
        serverHello.setVer(Const.SYS_VER);
        serverHello.setType(Const.TYPE_SERVER_HELLO);
        serverHello.setNonce(uuid);
        serverHello.setServer(sdkConfig.getServerInfo());
        serverHello.setChain(sdkConfig.getChain());
        serverHello.setAlg(sdkConfig.getAlg());
        Map<Integer, VCFilter[]> vcFilters = sdkConfig.getVcFilters();
        if (vcFilters != null && vcFilters.get(action) != null) {
            serverHello.setVCFilters(vcFilters.get(action));
        }
        return serverHello;
    }

    public void validateClientHello(ClientHello req) throws SDKException {
        if (!Const.SYS_VER.equals(req.getVer())) {
            throw new SDKException(Const.ERR_WRONG_VERSION);
        }
        if (!Const.TYPE_CLIENT_HELLO.equals(req.getType())) {
            throw new SDKException(Const.ERR_TYPE_NOT_SUPPORTED);
        }
        if (!Const.ACTION_AUTHORIZATION.equals(req.getAction()) && !Const.ACTION_CERTIFICATION.equals(req.getAction())) {
            throw new SDKException(Const.ERR_ACTION_NOT_SUPPORTED);
        }
    }

    public String[] validateClientHello(String chain, String presentation) throws Exception {
        DidProcessor didProcessor = didProcessors.get(chain);
        return didProcessor.getCredentialJsons(presentation);
    }

    public void validateClientResponse(ClientResponse res) throws Exception {
        validateClientResponseParams(res);

        String type = res.getType();
        Proof proof = res.getProof();
        Map<String, Object> didKeyAndIndex = getDIDKeyAndIndex(proof.getVerificationMethod());
        String did = (String) didKeyAndIndex.get("did");
        int index = (int) didKeyAndIndex.get("index");
        if (!res.getDid().equals(did)) {
            throw new SDKException(Const.DID_VERIFICATION_METHOD_NOT_MATCH);
        }
        String chain = getDIDChain(did);

        String nonce = res.getNonce();
        Integer action = getActionByNonce(nonce);

        ServerInfo serverInfo = sdkConfig.getServerInfo();
        ServerInfoToSign server = new ServerInfoToSign();
        server.setName(serverInfo.getName());
        server.setUrl(serverInfo.getUrl());
        server.setDid(serverInfo.getDid());
        ClientResponseMsg msg = new ClientResponseMsg();
        msg.setType(type);
        msg.setServer(server);
        msg.setNonce(nonce);
        msg.setDid(did);
        msg.setCreated(proof.getCreated());

        byte[] sigData = Helper.hexToBytes(proof.getValue());
        String dataToSign = JSON.toJSONString(msg);

        DidProcessor didProcessor = didProcessors.get(chain);
        didProcessor.verifySig(did, index, dataToSign.getBytes(), sigData);

        //verify presentation
        String[] vps = res.getVPs();
        if (vps != null && vps.length > 0) {
            VCFilter[] requiredTypes = sdkConfig.getVcFilters().get(action);
            for (int i = 0; i < vps.length; i++) {
                String vp = vps[i];
                didProcessor.verifyPresentation(vp, requiredTypes);
            }
        }
    }

    public void validateClientResponseParams(ClientResponse response) throws SDKException {
        if (!Const.SYS_VER.equals(response.getVer())) {
            throw new SDKException(Const.ERR_WRONG_VERSION);
        }
        if (!Const.TYPE_CLIENT_RESPONSE.equals(response.getType())) {
            throw new SDKException(Const.ERR_TYPE_NOT_SUPPORTED);
        }
    }

    public Map<String, Object> getDIDKeyAndIndex(String verifyMethod) throws SDKException {
        String[] tmpArr = verifyMethod.split("#");
        if (tmpArr.length != 2) {
            throw new SDKException(Const.VERIFICATION_METHOD_FORMAT_INVALID);
        }
        String[] keyArr = tmpArr[1].split("-");
        if (keyArr.length != 2) {
            throw new SDKException(Const.VERIFICATION_METHOD_FORMAT_INVALID);
        }
        Integer idx = Integer.valueOf(keyArr[1]);
        Map<String, Object> result = new HashMap<>();
        result.put("did", tmpArr[0]);
        result.put("index", idx);
        return result;
    }
}
