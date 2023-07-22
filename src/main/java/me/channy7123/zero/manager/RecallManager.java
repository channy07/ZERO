package me.channy7123.zero.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static me.channy7123.zero.Setting.getZero;
import static me.channy7123.zero.manager.StateManager.getState;

public class RecallManager implements Listener
{
    public static HashMap<Player, Location> returningState = new HashMap<Player, Location>();
    public static HashMap<Player, Location> returningZero = new HashMap<Player, Location>();

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e)
    {
        if(returningState.containsKey(e.getPlayer()))
        {
            Player p = e.getPlayer();

            if(!(returningState.get(p).getX() == p.getLocation().getX()
                    && returningState.get(p).getY() == p.getLocation().getY()
                    && returningState.get(p).getZ() == p.getLocation().getZ()))
            {
                returningState.remove(e.getPlayer());
                p.sendTitle(ChatColor.RED + "귀환이 취소되었습니다", "", 0, 30, 0);
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }

        if(returningZero.containsKey(e.getPlayer()))
        {
            Player p = e.getPlayer();

            if(!(returningZero.get(p).getX() == p.getLocation().getX()
                    && returningZero.get(p).getY() == p.getLocation().getY()
                    && returningZero.get(p).getZ() == p.getLocation().getZ()))
            {
                returningZero.remove(e.getPlayer());
                p.sendTitle(ChatColor.RED + "귀환이 취소되었습니다", "", 0, 30, 0);
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
    }

    public static void recallState(Player p)
    {
        if(getState(p) == null || p.getBedSpawnLocation() == null)
        {
            return;
        }

        if(returningState.containsKey(p))
        {
            return;
        }

        returningState.put(p, p.getLocation());

        new BukkitRunnable()
        {
            int i = 8;

            @Override
            public void run()
            {
                if(returningState.containsKey(p))
                {
                    if(i > 0)
                    {
                        p.sendTitle(ChatColor.GREEN + "귀환중.. " + i, "", 0, 30, 0);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                        i--;
                    }
                    else
                    {
                        p.resetTitle();
                        p.teleport(p.getBedSpawnLocation());
                        p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
                        returningState.remove(p);
                    }
                }
                else
                {
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Zero"), 0L, 20L);
    }

    public static void recallZero(Player p)
    {
        if(returningZero.containsKey(p))
        {
            return;
        }

        returningZero.put(p, p.getLocation());

        new BukkitRunnable()
        {
            int i = 8;

            @Override
            public void run()
            {
                if(returningZero.containsKey(p))
                {
                    if(i > 0)
                    {
                        p.sendTitle(ChatColor.GREEN + "귀환중.. " + i, "", 0, 30, 0);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                        i--;
                    }
                    else
                    {
                        p.resetTitle();
                        p.teleport(getZero());
                        p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
                        returningZero.remove(p);
                    }
                }
                else
                {
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Zero"), 0L, 20L);
    }
}
