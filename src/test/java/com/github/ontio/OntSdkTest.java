package com.github.ontio;

import com.github.ontio.did.DidProcessor;
import com.github.ontio.did.ont.OntProcessor;
import com.github.ontio.modules.ServerInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class OntSdkTest {
    private OntLoginSdk ontLoginSdk;


    @Before
    public void init() throws Exception {
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

        OntProcessor ontProcessor = new OntProcessor(true, "http://polaris2.ont.io:20334",
                "52df370680de17bc5d4262c446f102a0ee0d6312", "./wallet.json", "12345678");
        Map<String, DidProcessor> resolvers = new HashMap<>();
        resolvers.put("ont", ontProcessor);

        ontLoginSdk = new OntLoginSdk(sdkConfig, resolvers) {
            @Override
            String genRandomNonceFunc() {
                return UUID.randomUUID().toString();
            }

            @Override
            void checkNonceExistFunc(String nonce) {

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