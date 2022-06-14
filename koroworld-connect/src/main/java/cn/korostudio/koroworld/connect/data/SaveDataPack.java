package cn.korostudio.koroworld.connect.data;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 用于储存数据的数据包变种
 */
@Data
public class SaveDataPack extends CommandDataPack{
    /**
     * 储存所用标签。最长256字符，不然核心数据库将会报错。
     */
    String tags;
}
