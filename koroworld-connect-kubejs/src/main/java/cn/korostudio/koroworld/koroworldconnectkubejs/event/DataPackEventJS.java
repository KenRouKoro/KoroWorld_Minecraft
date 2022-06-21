package cn.korostudio.koroworld.koroworldconnectkubejs.event;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;

public class DataPackEventJS extends EventJS {

    protected String data;
    protected String tags;
    protected String target;
    protected String from;

    public final boolean post(String id, String tags,String data,String target,String from) {
        this.data=data;
        this.tags=tags;
        this.target = target;
        this.from = from;
        return this.post(ScriptType.SERVER, id);
    }
    public String getData(){
        return data;
    }

    public String getFrom() {
        return from;
    }

    public String getTags() {
        return tags;
    }

    public String getTarget() {
        return target;
    }
}
