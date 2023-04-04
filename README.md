# hero_story
hero_story
## 长连接
游戏服务器
可以主动推送数据
传输二进制
协议自己赞
暂用资源相对较多

## 短连接 
web 服务器
被迫营业
传输文本数据
http html 
占用资源相对较少 

## 消息协议 
编码--- 二进制数据---解码  
编解码是为了传输 和缩小体积  
编码之前做加密  解码之前做解密 
消息粘包 

## 本地调试地址 
http://cdn0001.afrxvk.cn/hero_story/demo/step040/index.html?serverAddr=127.0.0.1:12345

## 
System.out.println();
byte a = (byte) 1;
byte b = (byte) 2;
short i = (short) (a << 8 | b);
System.out.println(Integer.toBinaryString(i));

## 0, 0, 0, 13, 10, 1, 49, 18, 1, 49,
httpServerCodec() 在它内部保证了我们消息的完整性 
在内部实现了判断消息长度的过程   ， 前两位是代表消息长度  3，4 位代表消息的类型  ， 5.... 代表者消息体  
netty 怎么消息长度的  是 websocket 来保证  读消息的时候 会预处理下消息头   
在发消息之前websocket 会加上  ，socket   消息的长度是要我们自己处理的 

## 配置 protocbuf 的 环境变量  在path 
生成的命令
```shell
protoc.exe --java_out=. .\GameMsgProtocol.proto
```
生成在当前目录下  根据 当前目录下的 GameMsgProtocol.proto 文件 ‘



