package me.channy7123.zero.manager;

import me.channy7123.zero.UI.AttackItemUI;
import me.channy7123.zero.UI.TruceUI;
import me.channy7123.zero.enums.StateItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager implements Listener
{
    public static ArrayList<String> inWar = new ArrayList<String>();
    public static HashMap<String, ArrayList<String>> war = new HashMap<String, ArrayList<String>>();

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
        {
            if(p.getItemInHand() == null)
            {
                return;
            }

            ItemStack item = p.getItemInHand();

            if(!item.hasItemMeta())
            {
                return;
            }

            ItemMeta meta = item.getItemMeta();

            if(meta.equals(StateItem.ATTACK.getUseItem().getItemMeta()))
            {
                e.setCancelled(true);
                p.openInventory(AttackItemUI.attackUI());
            }
            else if(meta.equals(StateItem.TRUCE.getUseItem().getItemMeta()))
            {
                e.setCancelled(true);
                p.openInventory(TruceUI.truceUI(p));
            }
        }
    }
}
