编写Websocket聊天室功能，包括客户端JavaScript和服务器端Servlet

（1）	利用java编写websocket多人在线聊天，并用tomcat运行程序。

（2）	实现过程

①	服务端建立一个websocket，每当浏览器新页面访问，javascript的onopen方法会发送至服务器，创建一个新用户。使用一个Arraylist储存用户信息。

②	网页上的发送button会触发onMassage的js函数，服务端接收数据病广播给其他用户。
 
服务端通过判断接收的信息的头部实现：

＃list 打出所有在线用户
@name＋内容   可以私聊

学弟学妹不要直接抄作业orz
