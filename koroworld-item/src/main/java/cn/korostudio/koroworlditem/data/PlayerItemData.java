package cn.korostudio.koroworlditem.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerItemData {
    public PlayerItemData(ArrayList<String> mainArraylist, ArrayList<String> armorArraylist, ArrayList<String> offHandArraylist){
        this.mainArraylist = mainArraylist;
        this.armorArraylist = armorArraylist;
        this.offHandArraylist = offHandArraylist;
    }
    protected List<String> mainArraylist;
    protected List<String> armorArraylist;
    protected List<String> offHandArraylist;
}
