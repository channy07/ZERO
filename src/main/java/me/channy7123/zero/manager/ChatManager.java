package me.channy7123.zero.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import static me.channy7123.zero.Zero.config;

public class ChatManager implements Listener
{
    public static void setPlayerName(Player p, String name)
    {
        config.set(p.getUniqueId() + ".chatName", name);
    }

    public static String getPlayerName(Player p)
    {
        return config.getString(p.getUniqueId() + ".chatName");
    }

    public static void setPlayerNickname(Player p, String nickname)
    {
        config.set(p.getUniqueId() + ".chatNickname", nickname);
    }

    public static String getPlayerNickname(Player p)
    {
        return config.getString(p.getUniqueId() + ".chatNickname");
    }

    public static void setPlayerNicknameColor(Player p, ChatColor color)
    {
        char nickname = color.getChar();
        config.set(p.getUniqueId() + ".chatNicknameColor", nickname);
    }

    public static ChatColor getPlayerNicknameColor(Player p)
    {
        try
        {
            return ChatColor.getByChar(config.getString(p.getUniqueId() + ".chatNicknameColor"));
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static String getPlayerChatName(Player p)
    {
        if(getPlayerName(p) == null) return p.getName();
        else return getPlayerName(p) + ChatColor.GRAY + "(" + p.getName() + ")" + ChatColor.RESET;
    }

    @EventHandler
    public void PlayerChatEvent(PlayerChatEvent e)
    {
        e.setCancelled(true);

        final int ChatRange = 200; //채팅이 보이는 거리

        Player p = e.getPlayer();
        String chat = e.getMessage();

        if(StateManager.waitingForChat.containsKey(p))
        {
            return;
        }

        ChatColor color = ChatColor.RESET;
        String nickname = "";
        String name = p.getDisplayName();

        if(getPlayerNicknameColor(p) != null) color = getPlayerNicknameColor(p);

        if(getPlayerNickname(p) != null) nickname = "[" + color + getPlayerNickname(p) + ChatColor.RESET + "] ";

        if(getPlayerName(p) != null) name = getPlayerName(p);



        for(Player player : p.getWorld().getPlayers())
        {
            if(player.getLocation().distance(p.getLocation()) <= 200)
            {
                player.sendMessage(nickname + name + ChatColor.GRAY + "(" + p.getName() + ")" + ChatColor.RESET +" : " + chat);
            }
        }
    }

}
