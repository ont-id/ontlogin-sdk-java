# How to use ontlogin sdk java

## 1. 介绍

略



## 2. 在项目中集成ontlogin sdk

在项目中集成ontlogin sdk

只需要以下步骤：

1. 初始化sdk

2. 新增2个api:  

   1. requestChallenge: 用于请求服务的的挑战内容

   2. submitChallenge:提交对挑战内容的签名，以及服务端要求的VP（如果有）

      

3. 根据业务需求，映射DID和既有的用户ID，解析并保存VP中的数据。

### 2.1 集成的详细流程

ontlong-sdk-java : https://github.com/ontology-tech/ontlogin-sdk-java

本示例使用 SpringBoot 创建Restful 后台服务.

示例源码：https://github.com/ontology-tech/ontlogin-demo/tree/main/backend/java



1. 打包sdk依赖到本地Maven仓库：

```
mvn install:install-file -DgroupId=com.github.ontio -DartifactId=ontlogin-sdk-java -Dversion=1.0.0 -Dpackaging=jar -Dfile=ontlogin-sdk-java-1.0.0.jar
```
并在pom.xml 中加入依赖：
```
        <dependency>
            <groupId>com.github.ontio</groupId>
            <artifactId>ontlogin-sdk-java</artifactId>
            <version>1.0.0</version>
        </dependency>
 ```

2. 在Controller中加入申请挑战和提交挑战的restful接口

```
    //申请挑战
    @PostMapping("/challenge")
    public Result generateChallenge(@RequestBody ClientHello clientHello) throws Exception {
        String action = "generateChallenge";
        ServerHello result = loginService.generateChallenge(action, clientHello);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    //提交挑战
    @PostMapping("/validate")
    public Result validateClientResponse(@RequestBody ClientResponse clientResponse) throws Exception {
        String action = "validateClientResponse";
        String token = loginService.validateClientResponse(action, clientResponse);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), token);
    }

    //其他业务逻辑
    @PostMapping("/check-jwt")
        public Result checkJwt(@RequestBody JSONObject req) {
            String action = "checkJwt";
            String token = req.getString("token");
            loginService.checkJwt(action, token);
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
        }
```

3.实现loginService
```
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private SDKUtil sdkUtil;

    @Override
    public ServerHello generateChallenge(String action, ClientHello clientHello) throws Exception {
        //调用sdk生成挑战
        return sdkUtil.generateChallenge(clientHello);
    }

    @Override
    public String validateClientResponse(String action, ClientResponse clientResponse) throws Exception {
        sdkUtil.validateClientResponse(clientResponse);
        //用户的挑战验证通过
        //下面可以依据系统或业务来做不同的处理
        //如本示例使用JWT作为之后的权限验证
        String token = jwtUtils.signAccess("", "test user");
        return token;

    }

    @Override
    public void checkJwt(String action, String token) {
        jwtUtils.verifyAccessToken(token);
    }
```


4.SDKUtil，包含初始化ontlogin Sdk
```
    private OntLoginSdk sdk;
    //用于存储生成的挑战uuid，实际的项目中可以保存在数据库，redis,或者cache中
    private Map<String, Integer> nonceMap = new HashMap<>();

    private OntLoginSdk getOntLoginSdk() throws Exception {
        if (sdk == null) {
            synchronized (OntLoginSdk.class) {
                if (sdk == null) {
                    ServerInfo serverInfo = new ServerInfo();
                    serverInfo.setName("testServcer");
                    serverInfo.setIcon("http://somepic.jpg");
                    serverInfo.setUrl("https://ont.io");
                    //服务的DID 
                    serverInfo.setDid("did:ont:sampletest");
                    serverInfo.setVerificationMethod("");
                    
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
                            
                    SDKConfig sdkConfig = new SDKConfig();
                    //支持的链的名称， 如eth, ont, bsc等， 需要实现对应Processor
                    sdkConfig.setChain(new String[]{"ont"});
                    //支持的签名算法
                    sdkConfig.setAlg(new String[]{"ES256"});
                    //服务器的信息
                    sdkConfig.setServerInfo(serverInfo);
                    //服务端在注册时要求客户端提供的VC类型
                    sdkConfig.setVcFilters(vcFilters);
                    
                    //初始化链的Processor
                    //以ontology 为例，参数说明
                    //1. doubleDirection bool: 是否需要双向挑战认证
                    //2. ontology 节点rpc的服务地址
                    //3. did 的合约地址，如果doubleDirection 为false,可以为空
                    //4. ontology的钱包地址，如果doubleDirection 为false,可以为空
                    //5. 钱包密码，如果doubleDirection 为false,可以为空
                    OntProcessor ontProcessor = new OntProcessor(false, "http://polaris2.ont.io:20334",
                            "52df370680de17bc5d4262c446f102a0ee0d6312", "./wallet.json", "12345678");
                    Map<String, DidProcessor> resolvers = new HashMap<>();
                    resolvers.put("ont", ontProcessor);

                    //除了config，和Processor,sdk 还需要重写两个函数
                    //1. 传入action并生成UUID函数 public String genRandomNonceFunc(Integer action)
                    //2. 验证Nonce(UUID)是否存在与数据库/redis/缓存中，并返回action的函数 public Integer getActionByNonce(String nonce)
                    sdk = new OntLoginSdk(sdkConfig, resolvers) {
                        @Override
                        public String genRandomNonceFunc(Integer action) {
                            String nonce = UUID.randomUUID().toString().replace("-", "");
                            nonceMap.put(nonce, action);
                            return nonce;
                        }

                        @Override
                        public Integer getActionByNonce(String nonce) {
                            Integer action = nonceMap.get(nonce);
                            if (action == null) {
                                throw new OntLoginException("checkNonce", ErrorInfo.NONCE_NOT_EXISTS.descEN(), ErrorInfo.NONCE_NOT_EXISTS.code());
                            }
                            nonceMap.remove(nonce);
                            return action;
                        }
                    };
                }
            }
        }
        return sdk;
    }

    public ServerHello generateChallenge(ClientHello clientHello) throws Exception {
        OntLoginSdk ontLoginSdk = getOntLoginSdk();
        ServerHello serverHello = ontLoginSdk.generateChallenge(clientHello);
        return serverHello;
    }

    public void validateClientResponse(ClientResponse clientResponse) throws Exception {
        OntLoginSdk ontLoginSdk = getOntLoginSdk();
        ontLoginSdk.validateClientResponse(clientResponse);
    }
```

5.处理VP

如果要求客户端在挑战中包含所需要的VC，由于VC的格式并不固定，所以sdk仅提供从VP中抽取VC的JSON格式的文本的功能

```
public String[] getCredentialJsons(String presentation)
```

服务端可以根据约定好的格式来解析VC，做后续的业务处理

