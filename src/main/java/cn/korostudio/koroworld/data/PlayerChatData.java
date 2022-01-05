package cn.korostudio.koroworld.data;

import lombok.Data;

@Data
public class PlayerChatData {
    protected String server;
    protected String Name;
    protected String Message;
    protected boolean broadcast = false;
}
