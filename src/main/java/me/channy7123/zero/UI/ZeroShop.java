package me.channy7123.zero.UI;

import me.channy7123.zero.enums.StateItem;
import me.channy7123.zero.enums.WarItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.channy7123.zero.Setting.getStateShop;
import static me.channy7123.zero.Setting.getWarShop;
import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.enums.Items.consumeItem;

public class ZeroShop implements Listener
{
    @EventHandler
    public void InteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if(e.getClickedBlock().getLocation().equals(getStateShop()))
            {
                e.setCancelled(true);
                p.openInventory(getStateShopInventory());
            }
            else if(e.getClickedBlock().getLocation().equals(getWarShop()))
            {
                e.setCancelled(true);
                p.openInventory(getWarShopInventory());
            }

        }
    }

    @EventHandler
    public void PlayerInventoryClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null)
        {
            return;
        }

        if (e.getView().getTitle().equals("국가 상점") && e.getInventory().getSize() == 9*2)
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

            for(StateItem stateItem : StateItem.values())
            {
                if(e.getCurrentItem().equals(stateItem.getItem()))
                {
                    for(ItemStack stack : p.getInventory().getStorageContents())
                    {
                        if(stack == null)
                        {
                            if(consumeItem(p, stateItem.getPrice(), new ItemStack(stateItem.getMaterial())))
                            {
                                p.getInventory().addItem(stateItem.getUseItem());
                                p.updateInventory();
                            }
                            else
                            {
                                p.closeInventory();
                                p.sendMessage(alert + "금액이 부족합니다");
                            }

                            return;
                        }

                        if(stack.hasItemMeta())
                        {
                            if(stack.getItemMeta().equals(stateItem.getUseItem().getItemMeta()) && stack.getAmount() < 64)
                            {
                                if(consumeItem(p, stateItem.getPrice(), new ItemStack(stateItem.getMaterial())))
                                {
                                    p.getInventory().addItem(stateItem.getUseItem());
                                    p.updateInventory();
                                }
                                else
                                {
                                    p.closeInventory();
                                    p.sendMessage(alert + "금액이 부족합니다");
                                }

                                return;
                            }
                        }
                    }
                    p.sendMessage(alert + "인벤토리에 빈칸이 없습니다");
                }
            }
        }
        else if(e.getView().getTitle().equals("전쟁 상점") && e.getInventory().getSize() == 9*2)
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

            for(WarItem warItem : WarItem.values())
            {
                if(e.getCurrentItem().equals(warItem.getItem()))
                {
                    for(ItemStack stack : p.getInventory().getStorageContents())
                    {
                        if(stack == null)
                        {
                            if(consumeItem(p, warItem.getPrice(), new ItemStack(warItem.getMaterial())))
                            {
                                p.getInventory().addItem(warItem.getUseItem());
                                p.updateInventory();
                            }
                            else
                            {
                                p.closeInventory();
                                p.sendMessage(alert + "금액이 부족합니다");
                            }

                            return;
                        }

                        if(stack.hasItemMeta())
                        {
                            if(stack.getItemMeta().equals(warItem.getUseItem().getItemMeta()) && stack.getAmount() < 64)
                            {
                                if(consumeItem(p, warItem.getPrice(), new ItemStack(warItem.getMaterial())))
                                {
                                    p.getInventory().addItem(warItem.getUseItem());
                                    p.updateInventory();
                                }
                                else
                                {
                                    p.closeInventory();
                                    p.sendMessage(alert + "금액이 부족합니다");
                                }

                                return;
                            }
                        }
                    }

                    p.sendMessage(alert + "인벤토리에 빈칸이 없습니다");
                }
            }
        }
    }

    public Inventory getStateShopInventory()
    {
        Inventory inv = Bukkit.createInventory(null, 9*2, "국가 상점");

        inv.setItem(0, StateItem.BUILDING.getItem());
        inv.setItem(1, StateItem.ATTACK.getItem());
        inv.setItem(2, StateItem.TRUCE.getItem());

        return inv;
    }

    public Inventory getWarShopInventory()
    {
        Inventory inv = Bukkit.createInventory(null, 9*2, "전쟁 상점");

        inv.setItem(0, WarItem.ATTACK.getItem());
        inv.setItem(1, WarItem.HEAL.getItem());
        inv.setItem(2, WarItem.BOMB.getItem());

        return inv;
    }
}
