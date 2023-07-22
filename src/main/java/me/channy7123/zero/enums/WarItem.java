package me.channy7123.zero.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

import static me.channy7123.zero.enums.Items.getItemStack;

public enum WarItem
{
    ATTACK(getItemStack(Material.MAGMA_CREAM, ChatColor.YELLOW + "코어 공격 물약", 1,
            Arrays.asList(ChatColor.WHITE + "국가의 코어 체력을 1 감소시킵니다", ChatColor.YELLOW + "사용 : 인벤토리 클릭", ChatColor.AQUA + "가격 : 1 다이아"), true), Material.DIAMOND, 1),
    HEAL(getItemStack(Material.SLIME_BALL, ChatColor.YELLOW + "코어 회복 물약", 1,
            Arrays.asList(ChatColor.WHITE + "국가의 코어 체력을 1 회복시킵니다", ChatColor.YELLOW + "사용 : 인벤토리 클릭", ChatColor.AQUA + "가격 : 2 다이아"), true), Material.DIAMOND, 2),
    BOMB(getItemStack(Material.TNT, ChatColor.YELLOW + "블럭 제거기", 1,
         Arrays.asList(ChatColor.WHITE + "블럭을 중심으로 주변 1칸의 블럭을 제거합니다", ChatColor.YELLOW + "사용 : 블럭 설치", ChatColor.GRAY + "가격 : 10 철"), true), Material.IRON_INGOT, 10)
    ;

    private final ItemStack item;
    private final Material material;
    private final int price;

    WarItem(ItemStack item, Material material, int price)
    {
        this.item = item;
        this.price = price;
        this.material = material;
    }

    public ItemStack getItem()
    {
        return item;
    }

    public ItemStack getUseItem()
    {
        ItemStack item = getItem().clone();
        ItemMeta m = item.getItemMeta().clone();
        ArrayList<String> lore = (ArrayList<String>) m.getLore();
        lore.remove(lore.size() - 1);
        m.setLore(lore);
        item.setItemMeta(m);

        return item;
    }

    public int getPrice() { return price; }

    public Material getMaterial() { return material; }
}
