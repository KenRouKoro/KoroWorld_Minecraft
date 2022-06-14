package cn.korostudio.koroworld.connect.data;

import lombok.Data;

/**
 * 数据包基类
 */
@Data
public class DataPack {
    /**
     * 数据包UUID（用于标识当前数据包）
     */
    String UUID;
    /**
     * 数据包类型，这个值将会被服务核心的类型处理器处理，如服务核心没有对应的类型处理器，则该数据包将会发送至 target 值的位置
     */
    String type;
    /**
     * 数据包来源
     */
    String from;
    /**
     * 数据包目标，有两个特殊值 ALL和SERVERONLY，ALL代表转发到所有其他的服务器上，SERVERONLY表示不要转发。
     */
    String target;
    /**
     * 数据包的数据
     */
    String data;
}
