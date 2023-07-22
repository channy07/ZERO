package me.channy7123.zero.UI;

import me.channy7123.zero.State;
import me.channy7123.zero.enums.StateItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.enums.Items.useItem;
import static me.channy7123.zero.manager.StateManager.*;
import static me.channy7123.zero.manager.StateManager.getState;

public class TruceUI implements Listener
{
    public static Inventory truceUI(Player p)
    {
        Inventory inv = Bukkit.createInventory(null, 9*6, "휴전할 국가를 선택해 주세요");

        for(String name : getState(p).getWarStates())
        {
            State state = getState(name);

            if(state.getKing() != null)
            {
                ItemStack item = getHead(state.getKing().getName());
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(ChatColor.YELLOW + state.getName() + " 국가");

                item.setItemMeta(m);

                inv.addItem(item);
            }
            else
            {
                ItemStack item = getHead(state.getOfflineKing().getName());
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

        if(e.getView().getTitle().equals("휴전할 국가를 선택해 주세요") && e.getInventory().getSize() == truceUI(p).getSize())
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

            for(String name : getState(p).getWarStates())
            {
                State state = getState(name);

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
                        p.sendMessage(alert + "국가의 왕이 휴전을 요청할 수 있습니다");
                        p.closeInventory();

                        return;
                    }

                    if(!getState(p).getWarStates().contains(state.getName()))
                    {
                        p.sendMessage(alert + "그 국가와 전쟁중이 아닙니다");
                        p.closeInventory();

                        return;
                    }

                    if(waitingForTruce.containsKey(state.getName()))
                    {
                        p.sendMessage(alert + "그 국가는 이미 휴전 신청을 받았습니다");
                        p.closeInventory();

                        return;
                    }

                    if(state.getKing() == null)
                    {
                        getState(p).declareTruce(state);
                        useItem(p, StateItem.TRUCE.getUseItem());
                        p.closeInventory();

                        return;
                        }

                    waitingForTruce.put(state.getName(), getState(p).getName());
                    p.sendMessage(alert + state.getName() + "국가에 휴전 신청을 보냈습니다");

                    state.getKing().sendMessage(alert + getState(p).getName() + "국가의 휴전 신청을 받으시겠습니까?");

                    TextComponent message = new TextComponent("[예]");
                    message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zero truce"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("휴전을 수락합니다").create()));
                    state.getKing().spigot().sendMessage(message);

                    TextComponent message2 = new TextComponent("[아니요]");
                    message2.setColor(net.md_5.bungee.api.ChatColor.RED);
                    message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zero notruce"));
                    message2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("휴전을 거절합니다").create()));
                    state.getKing().spigot().sendMessage(message2);

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            if(waitingForTruce.containsKey(state.getName()))
                            {
                                waitingForTruce.remove(state.getName());
                                p.sendMessage(alert + state.getName() + "국가에 보낸 휴전 신청이 30초가 지나 거절되었습니다");
                                state.getKing().sendMessage(alert + getState(p).getName() + "국가의 휴전 신청이 30초가 지나 거절되었습니다");
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("Zero"), 20*30);

                    p.closeInventory();
                    useItem(p, StateItem.TRUCE.getUseItem());

                    p.updateInventory();
                }
            }
        }
    }


    public static ItemStack getHead(String name)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(name);
        skull.setOwner(name);
        item.setItemMeta(skull);

        return item;
    }
}
