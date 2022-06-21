package cn.korostudio.koroworld.koroworldconnectkubejs;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.connect.api.DataPackListener;
import cn.korostudio.koroworld.core.util.MessageTool;
import cn.korostudio.koroworld.koroworldconnectkubejs.event.DataPackEventJS;
import cn.korostudio.koroworld.koroworldconnectkubejs.util.DataPackUtil;
import cn.korostudio.koroworld.koroworldconnectkubejs.util.HttpKubejsUtil;
import cn.korostudio.koroworld.koroworldconnectkubejs.util.KubeJSDataPack;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

public class KoroWorldConnectKubejsPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        super.init();
        DataAPI.listen(new DataPackListener() {
            @Override
            public String getType() {
                return "KubeJS-DataPack";
            }

            @Override
            public void run(JSONObject jsonObject) {
                KubeJSDataPack js = JSONUtil.toBean(jsonObject,KubeJSDataPack.class);
                ThreadUtil.execute(()->{
                    (new DataPackEventJS()).post("KoroWorldDataPackListener",js.getTags(),js.getData(),js.getTarget(),js.getFrom());
                });
            }
        });
    }

    @Override
    public void addBindings(BindingsEvent event) {
        super.addBindings(event);
        event.add("KoroWorldDataAPI", DataAPI.class);
        event.add("MessageTool", MessageTool.class);
        event.add("ThreadUtil", ThreadUtil.class);
        event.add("HttpUtil", HttpKubejsUtil.class);
        event.add("KoroWorldDataPackUtil", DataPackUtil.class);

        if (event.type == ScriptType.STARTUP) {
            //event.addFunction("onForgeEvent", args -> onPlatformEvent(event, args), null, KubeJSForgeEventHandlerWrapper.class);
        }
    }

    @Override
    public void addClasses(ScriptType type, ClassFilter filter) {
        super.addClasses(type, filter);
        filter.allow("cn.korostudio.koroworld.connect.data");
        filter.allow("cn.korostudio.koroworld.connect.api.DataPackListener");
        filter.allow("cn.korostudio.koroworld.core.util");
        filter.allow("cn.hutool");

    }

    @Override
    public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        super.addTypeWrappers(type, typeWrappers);
    }
}
