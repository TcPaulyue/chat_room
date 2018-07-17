package com.chaofan.websocket.Web;

import com.chaofan.websocket.config.StatusCode;
import com.chaofan.websocket.module.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Chaofan at 2018/7/4 15:58
 * email:chaofan2685@qq.com
 **/
@ServerEndpoint(value = "/websocket")
@Component
public class MyWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //用以记录用户和房间号的对应关系
    private static HashMap<String,String> RoomForUser = new HashMap<String,String>();

    //用以记录房间和其中用户群的对应关系
    public static HashMap<String,CopyOnWriteArraySet<User>> UserForRoom = new HashMap<String,CopyOnWriteArraySet<User>>();


    /**
     * 连接建立成功调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        addOnlineCount();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        subOnlineCount();
        CopyOnWriteArraySet<User> users = getUsers(session);
        if (users!=null){
            String nick = "某人";
            for (User user : users) {
                if (user.getId().equals(session.getId())){
                    nick = user.getNickname();
                }
            }
            sendMessagesOther(users,"<b>系统</b>："+nick+"退出房间");
            sendMessagesOther(users,"&system&"+StatusCode.INTO_ROOM);
            User closeUser = getUser(session);
            users.remove(closeUser);
            RoomForUser.remove(session.getId());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 消息内容
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        Map<String,String> map = new Gson().fromJson(message, new TypeToken<HashMap<String,String>>(){}.getType());
        User user = null;
        switch (map.get("type")){
            case "msg" :
                user = getUser(session);
                CopyOnWriteArraySet<User> users = getUsers(session);
                sendMessagesOther(users,"<b>"+user.getNickname()+"</b>："+map.get("msg"));
                break;
            case "init":
                String room = map.get("room");
                String nick = map.get("nick");
                if (room != null && nick != null){
                    user = new User(session.getId(),nick,this);
                    if (UserForRoom.get(room) == null){
                        CopyOnWriteArraySet<User> roomUsers = new CopyOnWriteArraySet<>();
                        roomUsers.add(user);
                        UserForRoom.put(room,roomUsers);
                        RoomForUser.put(session.getId(),room);
                    }else {
                        UserForRoom.get(room).add(user);
                        RoomForUser.put(session.getId(),room);
                    }
                    sendMessagesAll(getUsers(session),"<b>系统</b>："+nick+"成功加入房间");
                    sendMessagesAll(getUsers(session),"&system&"+StatusCode.INTO_ROOM);
                }
                break;
        }
    }

    /**
     * 连接发生错误时的调用方法
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 获得在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }


    /**
     * 根据当前用户的session获得他所在房间的所有用户
     * @param session
     * @return
     */
    private CopyOnWriteArraySet<User> getUsers(Session session){
        String room = RoomForUser.get(session.getId());
        CopyOnWriteArraySet<User> users = UserForRoom.get(room);
        return users;
    }

    private User getUser(Session session){
        String room = RoomForUser.get(session.getId());
        CopyOnWriteArraySet<User> users = UserForRoom.get(room);
        for (User user : users){
            if (session.getId().equals(user.getId())){
                return user;
            }
        }
        return null;
    }

    /**
     * 给某个房间的所有人发送消息
     * @param users
     * @param message
     */
    private void sendMessagesAll(CopyOnWriteArraySet<User> users, String message){
        //群发消息
        for (User item : users) {
            try {
                item.getWebSocket().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个房间除自己外发送消息
     * @param users
     * @param message
     */
    private void sendMessagesOther(CopyOnWriteArraySet<User> users, String message){
        //群发消息
        for (User item : users) {
            if (item.getWebSocket() != this){
                try {
                    item.getWebSocket().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}