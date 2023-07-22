package me.channy7123.zero.manager;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.channy7123.zero.manager.StateManager.*;
import static me.channy7123.zero.Zero.alert;

public class PlayerMenuManager implements Listener
{
    public static HashMap<Player, Player> invitePlayer = new HashMap<Player, Player>();

    public enum MenuItem
    {
        INVITE(getItemStack(Material.PAPER, ChatColor.YELLOW + "국가에 초대하기", 1, null)),
        EJECT(getItemStack(Material.BARRIER, ChatColor.RED + "국가에서 추방하기", 1, null)),
        INDEPENDENT(getItemStack(Material.STRUCTURE_VOID, ChatColor.YELLOW + "무소속", 1, null))
        ;

        private final ItemStack item;

        MenuItem(ItemStack item) {
            this.item = item;
        }

        public ItemStack getItem()
        {
            return item;
        }
    }

    public ItemStack getBelong(Player p)
    {
        if(!notinState(p))
        {
            if(getState(p).getKing().equals(p))
            {
                return getItemStack(Material.BEACON, ChatColor.YELLOW + getState(p).getName(), 1, Arrays.asList("king"));
            }
            else
            {
                return getItemStack(Material.BEACON, ChatColor.YELLOW + getState(p).getName(), 1, null);
            }
        }
        else
        {
            return MenuItem.INDEPENDENT.getItem();
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractAtEntityEvent e)
    {
        if(!(e.getRightClicked() instanceof Player))
        {
            return;
        }

        Player p = e.getPlayer();
        Player clicked = (Player) e.getRightClicked();

        if(p.isSneaking())
        {
            Inventory inv = Bukkit.createInventory(null, 9*1, clicked.getName());

            inv.setItem(0, getHead(clicked));

            inv.setItem(8, getBelong(clicked));

            if(!notinState(p))
            {
                if(getState(p).getKing().equals(p))
                {
                    if(notinState(clicked))
                    {
                        inv.setItem(7, MenuItem.INVITE.getItem());
                    }
                    else if(getState(p).equal(getState(clicked)))
                    {
                        inv.setItem(7, MenuItem.EJECT.getItem());
                    }
                }
            }

            p.openInventory(inv);
        }
    }

    @EventHandler
     public void InventoryClickEvent(InventoryClickEvent e)
     {
         Player p = (Player) e.getWhoClicked();
         Inventory inv = e.getInventory();

         if(e.getCurrentItem() == null)
         {
             return;
         }

         for(Player player : Bukkit.getOnlinePlayers())
         {
             if(e.getView().getTitle().equals(player.getName()) && inv.getSize() == 9)
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

                 if(e.getCurrentItem().equals(MenuItem.INVITE.getItem()))
                 {
                     p.closeInventory();

                     if(invitePlayer.containsKey(player) || !notinState(player))
                     {
                         p.sendMessage(alert + ChatColor.RED + "국가의 초대를 받고있는 사람은 초대할 수 없습니다");

                         return;
                     }

                     invitePlayer.put(player, p);

                     player.sendMessage(alert + getState(p).getName() + "국가의 초대를 받으시겠습니까?");

                     TextComponent message = new TextComponent("[예]");
                     message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                     message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zero join"));
                     message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                             new ComponentBuilder("국가 초대를 수락합니다").create()));
                     player.spigot().sendMessage(message);

                     TextComponent message2 = new TextComponent("[아니요]");
                     message2.setColor(net.md_5.bungee.api.ChatColor.RED);
                     message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zero nojoin"));
                     message2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                             new ComponentBuilder("국가 초대를 거절합니다").create()));
                     player.spigot().sendMessage(message2);

                     new BukkitRunnable()
                     {
                         @Override
                         public void run()
                         {
                             if(invitePlayer.containsKey(player))
                             {
                                 invitePlayer.remove(player);
                                 player.sendMessage(alert + "국가 초대가 30초가 지나 거절되었습니다");
                             }
                         }
                     }.runTaskLater(Bukkit.getPluginManager().getPlugin("Zero"), 20*30);
                 }
                 else if(e.getCurrentItem().equals(MenuItem.EJECT.getItem()))
                 {
                     p.closeInventory();

                     if(getState(p).equal(getState(player)))
                     {
                         getState(p).removeMember(player);

                         p.sendMessage(alert + player.getName() + "님을 국가에서 추방했습니다");
                         player.sendMessage(alert + getState(p).getName() + "국가에서 추방되었습니다");
                     }
                 }
             }
         }

     }

    public static ItemStack getHead(Player player)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    private static ItemStack getItemStack(Material material, String name, int amount, List<String> lore)
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}
