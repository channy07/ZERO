package me.channy7123.zero.UI;

import me.channy7123.zero.enums.SettingItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static me.channy7123.zero.Zero.alert;

public class SettingUI implements Listener
{
    public static Inventory settingUI()
    {
        Inventory inv = Bukkit.createInventory(null, 9*2, "설정");

        inv.setItem(0, SettingItem.STATE_SHOP.getItem());
        inv.setItem(1, SettingItem.WAR_SHOP.getItem());
        inv.setItem(2, SettingItem.ZERO.getItem());

        return inv;
    }

    @EventHandler
    public void PlayerInventoryClickEvent(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equals("설정") && e.getInventory().getSize() == settingUI().getSize())
        {
            e.setCancelled(true);

            if(e.getClick() == ClickType.DOUBLE_CLICK)
            {
                return;
            }

            if(e.getClick() != ClickType.LEFT)
            {
                p.sendMessage(alert + ChatColor.RED + "인벤토리를 클릭은 좌클릭만 할 수 있습니다");
                return;
            }

            if(e.getCurrentItem() != null)
            {
                if(e.getCurrentItem().equals(SettingItem.STATE_SHOP.getItem()))
                {
                    p.closeInventory();
                    p.getInventory().addItem(SettingItem.STATE_SHOP.getItem());
                }
                else if(e.getCurrentItem().equals(SettingItem.WAR_SHOP.getItem()))
                {
                    p.closeInventory();
                    p.getInventory().addItem(SettingItem.WAR_SHOP.getItem());
                }
                else if(e.getCurrentItem().equals(SettingItem.ZERO.getItem()))
                {
                    p.closeInventory();
                    p.getInventory().addItem(SettingItem.ZERO.getItem());
                }
            }

            p.updateInventory();
        }
    }
}
