package cn.korostudio.koroworld.core.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public interface MessageTemplateValue {
    void changeValue(Map<String, String> values, ServerPlayerEntity player);

    void changeValueInAll(Map<String, String> values, ServerPlayerEntity player);
}
