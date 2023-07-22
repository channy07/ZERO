package me.channy7123.zero.manager;

import me.channy7123.zero.enums.WarItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.channy7123.zero.manager.StateManager.canBreakStateBlock;
import static me.channy7123.zero.manager.StateManager.canBreakZeroBlock;

public class BlockRemover implements Listener
{
    @EventHandler
    public void InteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if(p.getItemInHand() == null)
        {
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
        {
            if(p.getItemInHand().hasItemMeta())
            {
                if(p.getItemInHand().getItemMeta().equals(WarItem.BOMB.getUseItem().getItemMeta()))
                {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);

                    Entity grenade = p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
                    grenade.setVelocity(p.getLocation().getDirection().multiply(1));

                    new BukkitRunnable()
                    {
                        int i = 3;

                        @Override
                        public void run()
                        {
                            if(i > 0)
                            {
                                i--;
                                grenade.getWorld().playSound(grenade.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                            }
                            else
                            {
                                grenade.remove();
                                grenade.getWorld().playSound(grenade.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                                grenade.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, grenade.getLocation(), 1);

                                for(int i1=-1; i1<=1; i1++)
                                {
                                    for (int i2=-1; i2<=1; i2++)
                                    {
                                        for(int i3=-1; i3<=1; i3++)
                                        {
                                            Block block = grenade.getLocation().clone().add(i1, i2, i3).getBlock();

                                            if(canBreakStateBlock(block) && canBreakZeroBlock(block))
                                            {
                                                if(block.getType() != Material.BEDROCK)
                                                {
                                                    block.setType(Material.AIR);
                                                }
                                            }
                                        }
                                    }
                                }

                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Zero"), 0, 20);
                }
            }
        }
    }

    @EventHandler
    public void PlaceBlockEvent(BlockPlaceEvent e)
    {
        if(e.getItemInHand().hasItemMeta())
        {
            if(e.getItemInHand().getItemMeta().equals(WarItem.BOMB.getUseItem().getItemMeta()))
            {
                e.setCancelled(true);
            }
        }
    }
}
