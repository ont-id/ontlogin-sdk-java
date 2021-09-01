# ontlogin-sdk-java

## API instruction

### 1. NewOntLoginSdk

创建 OntLoginSdk实例

parameters:

```
SDKConfig                                               //sdk configuartion

Map<String,DidProcessor>                                //did processer map 

@Override
public String genRandomNonceFunc(Integer action)        //function to generate random nonce

@Override
public Integer getActionByNonce(String nonce)           //get action by nonce
```



return :

```OntLoginSdk```



说明：

SDKConfig：

```
public class SDKConfig {
	String[] chain;                         //支持的链的名称 如“ONT”，“ETH“，”BSC“等
    String[] alg;                           //支持的签名算法  "ES256","Ed25519" 等
    ServerInfo serverInfo;                  //服务器信息配置
    Map<Integer, VCFilter[]> vcFilters;     //认证/授权所需要的Verifiable Credential的过滤器信息
}
```

```
public class ServerInfo {
	String name;                    //服务器名称
	String icon;                    //图标  （可选）
	String url;                     //服务URL 
	String did;                     //服务器DID（可选）
	String verificationMethod;      //验证方法 （可选）
}
```

```
public class VCFilter {
	String type;                    //VC的类型 如“DegreeCredential”等
	String[] express;               //零知识证明表达式列表
	String[] trustRoots             //信任的VC发行方DID列表
	boolean required;               //是否必需   
}
```



## 2. GenerateChallenge

生成挑战

param:

```
public class ClientHello {
	String ver;                         //版本号
	String type;                        //固定为“ClientHello“
	int action;                         //0：认证 ， 1：授权
	ClientChallenge clientChallenge;    //客户端挑战，用于双向验证（可选）
}
```

return: ServerHello

```
public class ServerHello {
	String ver;                 //版本号
	String type;		        //固定为“ServerHello“
	String nonce;              //随机nonce字符串
	ServerInfo server;         //服务器信息 
	String[] chain;            //支持的链名称列表 
	String[] alg;              //支持的签名算法列表
	VCFilter[] VCFilters;      //VC的过滤列表（可选）  
	ServerProof serverProo     //服务器端证明，用于双向验证（可选）
	Extension extension;       //扩展字段（可选）
}
```



## 3. ValidateClientResponse

验证客户端响应

param:

```
public class ClientResponse {
	String ver;	        //版本号
	String type;        //固定为“ClientResponse“
	String did;         //用户DID
	String nonce;       //服务器生成的随机nonce字符串
	Proof proof;        //客户端签名信息
	String[] VPs;       //verifiable presenation 列表（可选）
}
```


说明：

```
public class Proof {
	String type;            //签名算法
	String verificatio      //did & key index 如："did:ont:alice#key-1"
	int created;            //时间戳unix
	String value;           //签名字符串HEX
}
```



验证逻辑：

1. 验证输入参数合法性
2. 验证nonce是否为服务端生成
3. 验证客户端签名
4. 验证所有的VP及其包含的VC的合法性
5. 验证所有必需的VC是否都已经提供



## 4. GetCredentialJson

取得VP中所有VC的JSON字符串

param:

```
chain  ,      //链的名称
presentation，//VP 的字符串
```

return:

```
string[],     //VC的JSON 字符串      
```