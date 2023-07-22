package me.channy7123.zero.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

import static me.channy7123.zero.enums.Items.getItemStack;

public enum StateItem
{
    BUILDING(getItemStack(Material.BEACON, "국가 건설", 1,
            Arrays.asList(ChatColor.WHITE + "스폰지역 n칸 밖에서 설치하여 국가를 설치합니다", ChatColor.YELLOW + "사용 : 설치", ChatColor.AQUA + "가격 : 20 다이아"), false), Material.DIAMOND, 20),
    ATTACK(getItemStack(Material.PAPER, ChatColor.AQUA + "전쟁권", 1,
            Arrays.asList(ChatColor.WHITE + "한 국가에 전쟁을 선포합니다", ChatColor.YELLOW + "사용 : 우클릭", ChatColor.AQUA + "가격 : 10 다이아"), true), Material.DIAMOND, 10),
    TRUCE(getItemStack(Material.PAPER, ChatColor.AQUA + "휴전권", 1,
          Arrays.asList(ChatColor.WHITE + "상대 국가의 왕의 동의를 받고 휴전을 선포합니다", ChatColor.YELLOW + "사용 : 우클릭", ChatColor.AQUA + "가격 : 5 다이아"), true), Material.DIAMOND, 5)
    ;

    private final ItemStack item;
    private final Material material;
    private final int price;

    StateItem(ItemStack item, Material material, int price)
    {
        this.item = item;
        this.material = material;
        this.price = price;
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


