package me.channy7123.zero.enums;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;

public class Items
{
    public static ItemStack getItemStack(Material material, String name, int amount, List<String> lore, boolean b)
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if(b)
        {
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean consumeItem(Player player, int count, ItemStack item)
    {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(item.getType());

        int found = 0;

        if(player.getInventory().getItemInOffHand().hasItemMeta() && item.hasItemMeta())
        {
            if(player.getInventory().getItemInOffHand().getItemMeta().equals(item.getItemMeta()))
            {
                found += player.getInventory().getItemInOffHand().getAmount();
            }
        }
        else if(!player.getInventory().getItemInOffHand().hasItemMeta() && !item.hasItemMeta())
        {
            if(player.getInventory().getItemInOffHand().getType() == item.getType())
            {
                found += player.getInventory().getItemInOffHand().getAmount();
            }
        }

        for (ItemStack stack : ammo.values())
        {
            if(stack.hasItemMeta() && item.hasItemMeta())
            {
                if(stack.getItemMeta().equals(item.getItemMeta()))
                {
                    found += stack.getAmount();
                }
            }
            else if(!stack.hasItemMeta() && !stack.hasItemMeta())
            {
                if (stack.getType() == item.getType())
                {
                    found += stack.getAmount();
                }
            }
        }

        if (count > found)
            return false;

        if(player.getInventory().getItemInOffHand().hasItemMeta() && item.hasItemMeta())
        {
            if(player.getInventory().getItemInOffHand().getItemMeta().equals(item.getItemMeta()))
            {
                int removed = Math.min(count, player.getInventory().getItemInOffHand().getAmount());
                count -= removed;

                if (player.getInventory().getItemInOffHand().getAmount() == removed)
                    player.getInventory().setItemInOffHand(null);
                else
                    player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() - removed);
            }
        }
        else if(!player.getInventory().getItemInOffHand().hasItemMeta() && !item.hasItemMeta())
        {
            if (player.getInventory().getItemInOffHand().getType() == item.getType())
            {
                int removed = Math.min(count, player.getInventory().getItemInOffHand().getAmount());
                count -= removed;

                if (player.getInventory().getItemInOffHand().getAmount() == removed)
                    player.getInventory().setItemInOffHand(null);
                else
                    player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() - removed);
            }
        }

        for (Integer index : ammo.keySet())
        {
            ItemStack stack = ammo.get(index);

            if(stack.hasItemMeta() && item.hasItemMeta())
            {
                if(stack.getItemMeta().equals(item.getItemMeta()))
                {
                    int removed = Math.min(count, stack.getAmount());
                    count -= removed;

                    if (stack.getAmount() == removed)
                        player.getInventory().setItem(index, null);
                    else
                        stack.setAmount(stack.getAmount() - removed);

                    if (count <= 0)
                        break;
                }
            }
            else if(!stack.hasItemMeta() && !stack.hasItemMeta())
            {
                if(stack.getType() == item.getType())
                {
                    int removed = Math.min(count, stack.getAmount());
                    count -= removed;

                    if (stack.getAmount() == removed)
                        player.getInventory().setItem(index, null);
                    else
                        stack.setAmount(stack.getAmount() - removed);

                    if (count <= 0)
                        break;
                }
            }
        }

        player.updateInventory();
        return true;
    }

    public static boolean useItem(Player p, ItemStack item)
    {
        if(p.getItemInHand() == null)
        {
            return false;
        }

        if(p.getItemInHand().hasItemMeta())
        {
            if(p.getItemInHand().getItemMeta().equals(item.getItemMeta()))
            {
                if(p.getItemInHand().getAmount() > 1)
                {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                else
                {
                    p.getItemInHand().setAmount(0);
                }
            }
        }

        return false;
    }
}
