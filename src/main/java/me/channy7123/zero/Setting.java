package me.channy7123.zero;

import me.channy7123.zero.enums.SettingItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.Zero.config;

public class Setting implements Listener
{
    @EventHandler
    public void PlayerPlaceBlockEvent(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();

        if(e.getItemInHand() == null)
        {
            return;
        }

        if(e.getItemInHand().equals(SettingItem.STATE_SHOP.getItem()))
        {
            setStateShop(e.getBlock().getLocation());
            p.sendMessage(alert + "국가 상점이 설정되었습니다");
        }
        else if(e.getItemInHand().equals(SettingItem.WAR_SHOP.getItem()))
        {
            setWarShop(e.getBlock().getLocation());
            p.sendMessage(alert + "전쟁 상점이 설정되었습니다");
        }
        else if(e.getItemInHand().equals(SettingItem.ZERO.getItem()))
        {
            setZero(e.getBlock().getLocation());
            p.sendMessage(alert + "제로 구역이 설정되었습니다");
        }
    }

    public static void setStateShop(Location loc)
    {
        config.set("setting.setStateShop.world", loc.getWorld().getName());
        config.set("setting.setStateShop.x", loc.getX());
        config.set("setting.setStateShop.y", loc.getY());
        config.set("setting.setStateShop.z", loc.getZ());
    }

    public static void setWarShop(Location loc)
    {
        config.set("setting.setWarShop.world", loc.getWorld().getName());
        config.set("setting.setWarShop.x", loc.getX());
        config.set("setting.setWarShop.y", loc.getY());
        config.set("setting.setWarShop.z", loc.getZ());
    }

    public static void setZero(Location loc)
    {
        config.set("setting.setZero.world", loc.getWorld().getName());
        config.set("setting.setZero.x", loc.getX());
        config.set("setting.setZero.y", loc.getY());
        config.set("setting.setZero.z", loc.getZ());
    }

    public static Location getStateShop()
    {
        return new Location(Bukkit.getWorld(config.getString("setting.setStateShop.world")),
                config.getDouble("setting.setStateShop.x"),
                config.getDouble("setting.setStateShop.y"),
                config.getDouble("setting.setStateShop.z"));
    }

    public static Location getWarShop()
    {
        return new Location(Bukkit.getWorld(config.getString("setting.setWarShop.world")),
                config.getDouble("setting.setWarShop.x"),
                config.getDouble("setting.setWarShop.y"),
                config.getDouble("setting.setWarShop.z"));
    }

    public static Location getZero()
    {
        return new Location(Bukkit.getWorld(config.getString("setting.setZero.world")),
                config.getDouble("setting.setZero.x"),
                config.getDouble("setting.setZero.y"),
                config.getDouble("setting.setZero.z"));
    }
}
