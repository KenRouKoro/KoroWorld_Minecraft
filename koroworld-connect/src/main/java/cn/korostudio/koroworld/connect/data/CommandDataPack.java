package cn.korostudio.koroworld.connect.data;

import lombok.Data;

/**
 * 数据包的Command变种
 */
@Data
public class CommandDataPack extends DataPack{
    {
        type="command";
    }

    /**
     * 命令字符串
     */
    String command;
}
