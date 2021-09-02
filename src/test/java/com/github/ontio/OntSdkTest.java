package com.github.ontio;

import com.github.ontio.did.DidProcessor;
import com.github.ontio.did.ont.OntProcessor;
import com.github.ontio.modules.Const;
import com.github.ontio.modules.ServerInfo;
import com.github.ontio.modules.VCFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class OntSdkTest {
    private OntLoginSdk ontLoginSdk;


    @Before
    public void init() throws Exception {
        Map<Integer, VCFilter[]> vcFilters = new HashMap<>();

        //配置不同的actionType 下的VC filter
        VCFilter vcFilter = new VCFilter();
        //VC type
        vcFilter.setType("EmailCredential");
        //是否必须
        vcFilter.setRequired(true);
        //发行方的DID
        vcFilter.setTrustRoots(new String[]{"did:ont:testdid"});
        VCFilter[] vcFiltersArray = {vcFilter};
        vcFilters.put(Const.ACTION_AUTHORIZATION, vcFiltersArray);

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("testServer");
        serverInfo.setIcon("http://somepic.jpg");
        serverInfo.setUrl("https://ont.io");
        serverInfo.setDid("did:ont:AxxTest");
        serverInfo.setVerificationMethod("did:ont:AxxTest");

        SDKConfig sdkConfig = new SDKConfig();
        sdkConfig.setChain(new String[]{"ont"});
        sdkConfig.setAlg(new String[]{"ES256"});
        sdkConfig.setServerInfo(serverInfo);
        sdkConfig.setVcFilters(vcFilters);

        OntProcessor ontProcessor = new OntProcessor(true, "http://polaris2.ont.io:20334",
                "52df370680de17bc5d4262c446f102a0ee0d6312", "./wallet.json", "12345678");
        Map<String, DidProcessor> processor = new HashMap<>();
        processor.put("ont", ontProcessor);

        ontLoginSdk = new OntLoginSdk(sdkConfig, processor) {
            @Override
            public String genRandomNonceFunc(Integer action) {
                return UUID.randomUUID().toString().replace("-", "");
            }

            @Override
            public Integer getActionByNonce(String nonce) {
                return 0;
            }
        };
    }


    @Test
    public void testJson() throws Exception {
        String chain = ontLoginSdk.getDIDChain("did:ont:testDid");
        System.out.println(chain);
        Assert.assertEquals("ont", chain);
    }

}