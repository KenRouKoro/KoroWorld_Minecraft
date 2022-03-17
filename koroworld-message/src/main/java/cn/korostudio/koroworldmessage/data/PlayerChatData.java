package cn.korostudio.koroworldmessage.data;

import lombok.Data;

@Data
public class PlayerChatData {
    protected String server;
    protected String name;
    protected String message;
    protected String group;
    protected boolean HighestAuthority=false;
    protected boolean system = false;
}
