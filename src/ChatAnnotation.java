/**
 * Created by tonga on 2017/3/14.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/deep")
public class ChatAnnotation {

    private static final String GUSET_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections = new CopyOnWriteArraySet<ChatAnnotation>();
    public ArrayList<String> users = new ArrayList<String>();
    private String nickname;
    Session session;


    public ChatAnnotation(){
        nickname = GUSET_PREFIX +connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session){
        this.session = session;
        connections.add(this);
        System.out.println(nickname+" 已登录");
        users.add(nickname);
        for(ChatAnnotation client : connections){

            if(!client.session.equals(session)){
                try {
                    client.session.getBasicRemote().sendText("<center><p style='font-size:15px'><span style='color:red'>"+nickname+"</span> 上线了</p></center>");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        sendList();
    }

    @OnMessage
    public void incoming(String msg){

        if(!msg.startsWith("USER ")){
            if (msg.startsWith("#list")){
                sendList();
            }
            else if (msg.startsWith("@")){
                StringTokenizer st=new StringTokenizer(msg.substring(1,msg.length())," ");
                String nn=st.nextToken();
                String realmsg="";
                while (st.hasMoreTokens()){
                    realmsg=realmsg+" "+st.nextToken();
                }
                System.out.println(realmsg);
                int flag=0;
                for(ChatAnnotation client : connections){
                    try {
                            if (client.nickname.equals(nn)) {
                                if(!client.nickname.equals(this.nickname)) {
                                client.session.getBasicRemote().sendText("<p><span style='color:blue'>" + nickname + "(personal):</span> " + realmsg + "</p>");
                                this.session.getBasicRemote().sendText("<p><span style='color:pink'>" + nickname + "(personal):</span> " + realmsg + "</p>");

                                }else {
                                    this.session.getBasicRemote().sendText("<p><center><span style='color:pink; font-size:15px;'>不要给自己发消息</span></center> </p>");
                                }
                                flag=1;
                            }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                if (flag!=1){
                    try {
                        this.session.getBasicRemote().sendText("<p><center><span style='color:pink; font-size:15px;'>没有这个用户</span></center> </p>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
            else {
                for(ChatAnnotation client : connections){
                    try {
                        if(!client.session.equals(session)){
                            client.session.getBasicRemote().sendText("<p><span style='color:blue'>"+nickname+":</span> "+msg+"</p>");
                        }else{
                            client.session.getBasicRemote().sendText("<p><span style='color:green'>"+nickname+":</span> "+msg+"</p>");
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        else{
            String n=nickname;
            users.remove(n);
            nickname=msg.substring(5, msg.length());
            users.add(nickname);
            for(ChatAnnotation client : connections){
                try {
                    if(!client.session.equals(session)){
                        client.session.getBasicRemote().sendText("<center><p style='font-size:15px'><span style='color:red'>"+n+" </span>更名为<span style='color:red'> "+nickname+"</p></center>");
                    }else{
                        client.session.getBasicRemote().sendText("<center><p style='font-size:15px'>成功更名为<span style='color:red'> "+nickname+"</p></center>");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void end(){
        connections.remove(this);
        System.out.println(nickname+" 已下线");
        users.remove(nickname);
        for(ChatAnnotation client : connections){

            if(!client.session.equals(session)){
                try {
                    client.session.getBasicRemote().sendText("<center><p style='font-size:15px'><span style='color:red'>"+nickname+"</span> 已下线</p></center>");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        sendList();
    }

    public void sendList(){
            String a="";
            for(ChatAnnotation client : connections){
                a=a+" "+client.nickname;
            }
        try {
            this.session.getBasicRemote().sendText("<center><p style='font-size:15px'><span style='color:red'>"+a+" </span>在线<span style='color:red'> "+"</p></center>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
