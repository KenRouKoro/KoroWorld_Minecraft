package cn.korostudio.koroworlditem.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/**
 * 一个简单的 {@code Inventory} 实现，仅有默认的方法和物品列表获取器。
 *
 * Originally by Juuz
 */
public interface ImplementedInventory extends Inventory {

    /**
     * 从此物品栏中检索物品。
     * 每次被调用时必须返回相同实例。
     */
    DefaultedList<ItemStack> getItems();

    /**
     * 从物品列表创建物品栏。
     */
    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    /**
     * 根据指定的尺寸创建新的物品栏。
     */
    static ImplementedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    /**
     * 返回物品栏的大小。
     */
    @Override
    default int size() {
        return getItems().size();
    }

    /**
     * 检查物品栏是否为空。
     * @return true，如果物品栏仅有一个空堆，否则为true。
     */
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检索槽位中的物品。
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * 从物品栏槽位移除物品。
     * @param slot  从该槽位移除。
     * @param count 需要移除的物品个数。如果槽位中的物品少于需要的，则将其全部取出。
     */
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * 从物品栏槽位移除所有物品。
     * @param slot 从该槽位移除。
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * 将物品栏槽位中的当前物品堆替换为提供的物品堆。
     * @param slot  替换该槽位的物品堆。
     * @param stack 替换后新的物品堆。如果堆对于此物品栏过大（{@link Inventory#getMaxCountPerStack()}），则压缩为物品栏的最大数量。
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    /**
     * 清除物品栏。
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    /**
     * 将方块状态标记为脏。
     * 更改物品栏之后必须调用，所以游戏正确地储存物品栏内容并提取邻近方块物品栏改变。
     */
    @Override
    default void markDirty() {
        // 需要行为时，覆盖此方法。
    }

    /**
     * @return true 如果玩家可以使用物品栏，否则为 false。i
     */
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}