package me.channy7123.zero.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static me.channy7123.zero.enums.Items.getItemStack;

public enum SettingItem
{
    STATE_SHOP(getItemStack(Material.CHEST, "국가 상점 설정", 1, Arrays.asList("설치하여 국가 상점을 설정합니다"), false)),
    WAR_SHOP(getItemStack(Material.CHEST, "전쟁 상점 설정", 1, Arrays.asList("설치하여 전쟁 상점을 설정합니다"), false)),
    ZERO(getItemStack(Material.EMERALD_BLOCK, "제로 구역 설정", 1, Arrays.asList("설치하여 제로 구역 스폰을 설정합니다"), false))
    ;

    private final ItemStack item;

    SettingItem(ItemStack item)
    {
        this.item = item;
    }

    public ItemStack getItem()
    {
        return item;
    }
}
