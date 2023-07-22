package me.channy7123.zero.UI;

import me.channy7123.zero.State;
import me.channy7123.zero.enums.WarItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.channy7123.zero.enums.Items.consumeItem;
import static me.channy7123.zero.manager.StateManager.getState;
import static me.channy7123.zero.manager.StateManager.getStates;
import static me.channy7123.zero.Zero.alert;

public class BeaconUI implements Listener
{
    public static HashMap<String, Integer> stateAttackCooldown = new HashMap<>();
    public static HashMap<String, Integer> stateHealCooldown = new HashMap<>();

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking())
        {
            Location loc = e.getClickedBlock().getLocation();

            if(getState(loc) != null)
            {
                e.setCancelled(true);

                State state = getState(loc);

                if(state.getMembers().contains(p))
                {
                    p.openInventory(beaconUI(state));
                    return;
                }
                else
                {
                    for(String name : state.getWarStates())
                    {
                        State state2 = getState(name);

                        if(state2.getMembers().contains(p))
                        {
                            p.openInventory(beaconUI(state));
                            return;
                        }
                    }

                    p.sendMessage(alert + "다른 나라의 신호기는 열 수 없습니다");
                }
            }
        }
        else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Location loc = e.getClickedBlock().getLocation();

            if(getState(loc) != null)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerInventoryClickEvent(InventoryClickEvent e)
    {
        for(State state : getStates())
        {
            if(e.getView().getTitle().equals(state.getName()) && e.getInventory().getSize() == 9)
            {
                e.setCancelled(true);

                if(e.getClick() == ClickType.DOUBLE_CLICK)
                {
                    return;
                }

                Player p = (Player) e.getWhoClicked();

                if(e.getClick() != ClickType.LEFT)
                {
                    p.sendMessage(alert + org.bukkit.ChatColor.RED + "인벤토리를 클릭은 좌클릭만 할 수 있습니다");
                    return;
                }

                if(e.getCurrentItem() != null)
                {
                    if(e.getCurrentItem().hasItemMeta())
                    {
                        ItemStack item = e.getCurrentItem();

                        if(item.getItemMeta().equals(WarItem.ATTACK.getUseItem().getItemMeta()))
                        {
                            if(state.equal(getState(p)))
                            {
                                p.closeInventory();
                                p.sendMessage(alert + ChatColor.RED + "자신 국가의 코어를 공격할 수 없습니다");
                            }
                            else
                            {
                                if(stateAttackCooldown.containsKey(state.getName()))
                                {
                                    p.sendMessage(alert + ChatColor.RED + "공격 쿨타임 중입니다");
                                    return;
                                }

                                state.attackBeacon(p, 1);
                                consumeItem(p, 1, WarItem.ATTACK.getUseItem());

                                new BukkitRunnable()
                                {
                                    int i = 30;

                                    @Override
                                    public void run()
                                    {
                                        if(i > 0)
                                        {
                                            stateAttackCooldown.put(state.getName(), i);
                                            i--;

                                            updateBeaconUI(state);
                                        }
                                        else
                                        {
                                            stateAttackCooldown.remove(state.getName());

                                            updateBeaconUI(state);

                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Zero"), 0, 20);

                                for(Player player : state.getMembers())
                                {
                                    if(player.isOnline())
                                    {
                                        player.sendMessage(alert + "누군가 국가의 코어를 공격했습니다. (" + state.getHealth() + "/" + state.getMaxHealth() + ")");
                                    }
                                }

                                for(String name : state.getWarStates())
                                {
                                    State s = getState(name);

                                    for(Player player : s.getMembers())
                                    {
                                        if(player.isOnline())
                                        {
                                            player.sendMessage(alert + state.getName() + "국가의 코어를 누군가 공격했습니다. (" + state.getHealth() + "/" + state.getMaxHealth() + ")");
                                        }
                                    }
                                }
                            }
                        }
                        else if(item.getItemMeta().equals(WarItem.HEAL.getUseItem().getItemMeta()))
                        {
                            if(state.getHealth() == state.getMaxHealth())
                            {
                                p.sendMessage(alert + ChatColor.RED + "이미 최대 체력입니다");
                                return;
                            }

                            if(stateHealCooldown.containsKey(state.getName()))
                            {
                                p.sendMessage(alert + ChatColor.RED + "회복 쿨타임 중입니다");
                                return;
                            }

                            state.healBeacon(p, 1);
                            consumeItem(p, 1, WarItem.HEAL.getUseItem());

                            new BukkitRunnable()
                            {
                                int i = 30;

                                @Override
                                public void run()
                                {
                                    if(i > 0)
                                    {
                                        stateHealCooldown.put(state.getName(), i);
                                        i--;

                                        updateBeaconUI(state);
                                    }
                                    else
                                    {
                                        stateHealCooldown.remove(state.getName());

                                        updateBeaconUI(state);

                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Zero"), 0, 20);

                            for(Player player : state.getMembers())
                            {
                                if(player.isOnline())
                                {
                                    player.sendMessage(alert + "누군가 국가의 코어를 회복했습니다. (" + state.getHealth() + "/" + state.getMaxHealth() + ")");
                                }
                            }

                            for(String name : state.getWarStates())
                            {
                                State s = getState(name);

                                for(Player player : s.getMembers())
                                {
                                    if(player.isOnline())
                                    {
                                        player.sendMessage(alert + state.getName() + "국가의 코어를 누군가 회복했습니다. (" + state.getHealth() + "/" + state.getMaxHealth() + ")");
                                    }
                                }
                            }
                        }
                    }
                }

                p.updateInventory();
            }
        }
    }

    public static Inventory beaconUI(State state)
    {
        Inventory inv = Bukkit.createInventory(null, 9*1, state.getName());

        inv.setItem(4, getBeaconItem(state));

        return inv;
    }

    public static ItemStack getBeaconItem(State state)
    {
        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.RED + "" + state.getHealth() + "/" + state.getMaxHealth());

        if(stateAttackCooldown.containsKey(state.getName()))
        {
            lore.add(ChatColor.RED + "공격 쿨타임 : " + stateAttackCooldown.get(state.getName()) + "초");
        }

        if(stateHealCooldown.containsKey(state.getName()))
        {
            lore.add(ChatColor.GREEN + "회복 쿨타임 : " + stateHealCooldown.get(state.getName()) + "초");
        }

        return getItemStack(Material.RED_TERRACOTTA, ChatColor.YELLOW + state.getName() + "국가의 코어", 1, lore);
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

    public static void updateBeaconUI(State state)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(p.getOpenInventory() != null)
            {
                if(p.getOpenInventory().getTitle().equals(state.getName()) && p.getOpenInventory().getTopInventory().getSize() == 9)
                {
                    p.getOpenInventory().getTopInventory().setItem(4, getBeaconItem(state));
                }
            }
        }
    }
}
