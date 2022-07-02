package cn.korostudio.koroworld.chatsyn.data;

import cn.korostudio.koroworld.connect.data.DataPack;
import lombok.Data;

@Data
public class ChatDataPack extends DataPack {
    {
        setType("ChatDataPack");
    }
    String PlayerUUID;
    String PlayerName;
    String ServerName;
    String Group;
}
