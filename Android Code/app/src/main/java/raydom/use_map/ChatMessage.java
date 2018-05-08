package raydom.use_map;

import java.util.Date;

/**
 * Created by Administrator on 2017-09-08.
 */

public class ChatMessage {
    private String msg;
    private String name;
    private String time;

    public ChatMessage(String msg, String name,String time) {
        this.msg = msg;
        this.name = name;
        this.time = time;
    }

    public ChatMessage(){

    }

    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
