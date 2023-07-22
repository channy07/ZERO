package me.channy7123.zero.UI;

import me.channy7123.zero.State;
import me.channy7123.zero.enums.StateItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import static me.channy7123.zero.manager.StateManager.*;
import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.enums.Items.useItem;

public class AttackItemUI implements Listener
{
    public static Inventory attackUI()
    {
        Inventory inv = Bukkit.createInventory(null, 9*6, "전쟁할 국가를 선택해 주세요");

        for(State state : getStates())
        {
            if(state.getKing() != null)
            {
                ItemStack item = getHead(state.getKing());
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(ChatColor.YELLOW + state.getName() + " 국가");

                item.setItemMeta(m);

                inv.addItem(item);
            }
        }

        return inv;
    }

    @EventHandler
    public void PlayerInventoryClickEvent(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null)
        {
            return;
        }

        if(e.getView().getTitle().equals("전쟁할 국가를 선택해 주세요") && e.getInventory().getSize() == attackUI().getSize())
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

            for(State state : getStates())
            {
                if (state.getKing().isOnline())
                {
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + state.getName() + " 국가"))
                    {
                        if(notinState(p))
                        {
                            p.sendMessage(alert + "국가에 소속되어있지 않습니다");
                            p.closeInventory();

                            return;
                        }

                        if(!getState(p).getKing().equals(p))
                        {
                            p.sendMessage(alert + "국가의 왕이 전쟁을 걸 수 있습니다");
                            p.closeInventory();

                            return;
                        }

                        if(getState(p).equal(state))
                        {
                            p.closeInventory();
                            p.sendMessage(alert + "자신의 국가에 전쟁을 걸 수 없습니다");

                            return;
                        }

                        if(getState(p).getWarStates().contains(state.getName()))
                        {
                            p.closeInventory();
                            p.sendMessage(alert + "이미 그 국가와 전쟁중입니다");

                            return;
                        }

                        getState(p).declareWar(state);
                        p.closeInventory();
                        useItem(p, StateItem.ATTACK.getUseItem());
                    }
                }
            }

            p.updateInventory();
        }
    }

    public static ItemStack getHead(Player p)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(p.getName());
        skull.setOwner(p.getName());
        item.setItemMeta(skull);

        return item;
    }
}
