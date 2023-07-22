package me.channy7123.zero.manager;

import me.channy7123.zero.State;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.channy7123.zero.manager.ChatManager.*;
import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.Zero.config;

public class StateManager implements Listener
{
    public static HashMap<Player, Location> waitingForChat = new HashMap<Player, Location>();
    public static HashMap<String, String> waitingForTruce = new HashMap<String, String>();

    private double stateDistance = 200;
    private double bedDistance = 25;

    @EventHandler
    public void PlayerPlaceBlockEvent(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();

        if(!p.isOp())
        {
            if(!canBreakZeroBlock(e.getBlock()))
            {
                e.setCancelled(true);
            }
        }

        for(State state : getStates())
        {
            if(state.isInState(e.getBlock().getLocation()))
            {
                if(!state.canAccess(p))
                {
                    e.setCancelled(true);

                    p.sendMessage(alert + "다른 국가에는 블럭을 설치할 수 없습니다");
                }
            }
        }

        Location loc = e.getBlock().getLocation();
        loc.setY(0);

        if(e.getBlockPlaced().getType() == Material.BEACON /*&& e.getItemInHand().getItemMeta().getDisplayName().equals("국가 설정")*/)
        {
            if(e.getBlockPlaced().equals(p.getWorld().getHighestBlockAt(e.getBlockPlaced().getLocation())))
            {
                if(notinState(p))
                {
                    boolean b = true;

                    for(State state : getStates())
                    {
                        Location loc2 = state.getLocation();
                        loc2.setY(0);

                        if(state.getLocation().distance(loc) < stateDistance)
                        {
                            b = false;
                        }
                    }

                    if(b)
                    {
                        if(loc.distance(new Location(loc.getWorld(), 0, 0, 0)) > stateDistance + 100)
                        {
                            if(e.getBlock().getLocation().getY() >= 0 && e.getBlock().getLocation().getY() <= 200)
                            {
                                if(!loc.getWorld().getName().endsWith("_nether") && !loc.getWorld().getName().endsWith("_the_end"))
                                {
                                    new BukkitRunnable()
                                    {
                                        int i = 0;

                                        @Override
                                        public void run()
                                        {
                                            if(i == 0)
                                            {
                                                p.sendMessage(alert + "채팅으로 건설할 국가의 이름을 설정해 주세요(15자 이내)");
                                                p.sendMessage(alert + "설치가 완료되면 아래에 철블록이 설치됩니다");
                                                waitingForChat.put(p, e.getBlock().getLocation());
                                            }

                                            if(!waitingForChat.containsKey(p))
                                            {
                                                this.cancel();
                                            }

                                            if(i == 20*60)
                                            {
                                                waitingForChat.remove(p);
                                                p.sendMessage(alert + "60초가 다 되어 국가 건설이 취소되었습니다.");
                                                e.getBlockPlaced().setType(Material.AIR);
                                                e.getBlockPlaced().getWorld().dropItem(e.getBlockPlaced().getLocation().add(0.5, 0, 0.5), new ItemStack(Material.BEACON));

                                                this.cancel();
                                            }

                                            i++;
                                        }
                                    }.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Zero"), 0L, 1L);
                                }
                                else
                                {
                                    e.setCancelled(true);
                                    p.sendMessage(alert + ChatColor.RED + "올바른 월드가 아닙니다 ");
                                }
                            }
                            else
                            {
                                e.setCancelled(true);
                                p.sendMessage(alert + ChatColor.RED + "올바른 y좌표가 아닙니다 " + ChatColor.WHITE + "(y : 0 ~ 200)");
                            }
                        }
                        else
                        {
                            e.setCancelled(true);
                            p.sendMessage(alert + ChatColor.RED + "제로 구역과 너무 인접해있습니다");
                        }
                    }
                    else
                    {
                        e.setCancelled(true);
                        p.sendMessage(alert + ChatColor.RED + "다른 국가와 너무 인접해 있습니다");
                    }
                }
                else
                {
                    e.setCancelled(true);
                    p.sendMessage(alert + ChatColor.RED + "이미 국가에 소속되어있습니다");
                }
            }
            else
            {
                e.setCancelled(true);
                p.sendMessage(alert + "국가 신호기는 가장 높은곳에만 설치할 수 있습니다");
            }
        }
    }

    @EventHandler
    public void ChatEvent(PlayerChatEvent e)
    {
        Player p = e.getPlayer();

        if(waitingForChat.containsKey(e.getPlayer()))
        {
            e.setCancelled(true);

            if(e.getMessage().length() <= 15)
            {
                for(State state : getStates())
                {
                    if(state.getName().equalsIgnoreCase(e.getMessage()))
                    {
                        p.sendMessage(alert + ChatColor.RED + "이미 있는 이름입니다");
                        return;
                    }
                }
                createState(p, e.getMessage(), waitingForChat.get(p));
                waitingForChat.remove(p);
            }
            else
            {
                p.sendMessage(alert + ChatColor.RED + "15자 이내로 설정해주세요");
            }
        }
    }

    @EventHandler
    public void PlayerDamageEvent(EntityDamageByEntityEvent e)
    {
        if((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player))
        {
            Player p = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            Location loc2 = p.getLocation().clone();
            loc2.setX(0); loc2.setZ(0);

            if(p.getLocation().distance(loc2) <= 51)
            {
                e.setCancelled(true);
            }

            if(notinState(p) || notinState(damager))
            {
                return;
            }

            if(!p.equals(damager) && getState(p).equal(getState(damager)))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ProjectileDamageEvent(ProjectileHitEvent e)
    {
        if(e.getEntity().getShooter() instanceof Player && e.getHitEntity() instanceof Player)
        {
            Player p = (Player) e.getHitEntity();
            Player damager = (Player) e.getEntity().getShooter();

            if(notinState(p) || notinState(damager))
            {
                return;
            }

            if(!p.equals(damager) && getState(p).equal(getState(damager)))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerBreakBlockEvent(BlockBreakEvent e)
    {
        Player p = e.getPlayer();

        for(State state : getStates())
        {
            if(state.isInState(e.getBlock().getLocation()))
            {
                if(!state.canAccess(p))
                {
                    e.setCancelled(true);

                    p.sendMessage(alert + ChatColor.RED + "다른 국가에는 블럭을 파과할 수 없습니다");
                }
                else if(!state.equal(getState(p)))
                {
                    if(e.getBlock().getBlockData() instanceof Bed)
                    {
                        e.setCancelled(true);

                        p.sendMessage(alert + ChatColor.RED + "침대는 파괴할 수 없습니다");
                    }
                }
            }
        }

        if(e.getBlock().getType().equals(Material.BEACON))
        {
            if(getState(e.getBlock().getLocation()) != null)
            {
                e.setCancelled(true);
                p.sendMessage(alert + "국가 신호기는 파괴할 수 없습니다");
            }
            else
            {
                for(Location loc : waitingForChat.values())
                {
                    if(e.getBlock().getLocation().equals(loc))
                    {
                        e.setCancelled(true);
                        p.sendMessage(alert + "설치중인 국가 신호기는 파괴할 수 없습니다");
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();

        for(State state : getStates())
        {
            if(state.isInState(p.getLocation()))
            {
                if(!state.equal(getState(p)))
                {
                    if(!p.isGlowing())
                    {
                        p.sendTitle(ChatColor.RED + state.getName() + "국가에 침입했습니다", "발광효과에 걸립니다", 10, 30, 10);

                        for(Player player : state.getMembers())
                        {
                            if(player != null)
                            {
                                if(player.isOnline())
                                {
                                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                    player.sendTitle(ChatColor.RED + "누군가 국가에 침입했습니다", "", 10, 30, 10);
                                    player.sendMessage(alert + ChatColor.RED + "누군가 국가에 침입했습니다");
                                }
                            }
                        }
                    }

                    p.setGlowing(true);

                    return;
                }
            }
        }

        p.setGlowing(false);
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if(e.getClickedBlock() == null)
        {
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if(e.getClickedBlock().getType().equals(Material.BEACON))
            {
                if(getState(e.getClickedBlock().getLocation()) == null)
                {
                    for(Location loc : waitingForChat.values())
                    {
                        if(e.getClickedBlock().getLocation().equals(loc))
                        {
                            e.setCancelled(true);
                            p.sendMessage(alert + "설치중인 국가 신호기에는 상호작용할 수 없습니다");
                        }
                    }
                }
            }
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR)
        {
            for(State state : getStates())
            {
                if(state.isInState(e.getClickedBlock().getLocation()))
                {
                    if(!state.canAccess(p))
                    {
                        e.setCancelled(true);

                        p.sendMessage(alert + "다른 국가에는 상호작용할 수 없습니다");
                    }
                }
            }
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (Tag.BEDS.isTagged(e.getClickedBlock().getType()))
            {
                if(notinState(p))
                {
                    e.setCancelled(true);
                    p.sendMessage(alert + "국가에 소속되어있지 않아 스폰포인트를 설정할 수 없습니다");
                }

                for(State state : getStates())
                {
                    if(state.isInState(e.getClickedBlock().getLocation()))
                    {
                        if(!state.equal(getState(p)))
                        {
                            e.setCancelled(true);
                            p.sendMessage(alert + "올바르지 않은 스폰포인트 입니다");
                        }
                        else if(state.getLocation().distance(e.getClickedBlock().getLocation()) < bedDistance)
                        {
                            e.setCancelled(true);
                            p.sendMessage(alert + "올바르지 않은 스폰포인트 입니다(국가 코어와 너무 가깝습니다)");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent e)
    {
        Player p = e.getPlayer();

        for(State state : getStates())
        {
            if(state.isInState(e.getRightClicked().getLocation()))
            {
                if(!state.canAccess(p))
                {
                    e.setCancelled(true);

                    p.sendMessage(alert + "다른 국가에는 상호작용할 수 없습니다");
                }
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e)
    {
        Block b = e.getBlock();

        if(!e.getPlayer().isOp())
        {
            if(!canBreakZeroBlock(b))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e)
    {
        Iterator<Block> bl = e.blockList().iterator();

        while(bl.hasNext())
        {
            Block b = bl.next();

            if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
            {
                bl.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e)
    {
        Iterator<Block> bl = e.blockList().iterator();

        while(bl.hasNext())
        {
            Block b = bl.next();

            if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
            {
                bl.remove();
            }
        }
    }

    @EventHandler
    public void PistionEvent(BlockPistonExtendEvent e)
    {
        for(Block b : e.getBlocks())
        {
            if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PistionPullEvent(BlockPistonRetractEvent e)
    {
        for(Block b : e.getBlocks())
        {
            if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerBucketFillEvent(PlayerBucketFillEvent e)
    {
        Block b = e.getBlock();

        if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerBucketEmptyEvent(PlayerBucketEmptyEvent e)
    {
        Block b = e.getBlock();

        if(!canBreakStateBlock(b) || !canBreakZeroBlock(b))
        {
            e.setCancelled(true);
        }
    }

    public static boolean notinState(Player p)
    {
        for(State state : getStates())
        {
            ArrayList<String> members = (ArrayList<String>) config.getStringList("state." + state.getName() + ".member");

            if (members.contains(p.getUniqueId() + ""))
            {
                return false;
            }
        }

        return true;
    }

    public static void createState(Player p, String name, Location loc)
    {
        loc.clone().add(-1, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(-1, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(-1, -1, 1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(0, -1, 1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, -1).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, 0).getBlock().setType(Material.IRON_BLOCK);
        loc.clone().add(1, -1, 1).getBlock().setType(Material.IRON_BLOCK);

        for(double d=loc.getY()+1; d<=320; d++)
        {
            Location location = loc.clone();
            location.setY(d);
            location.getBlock().setType(Material.GLASS_PANE);
        }

        ArrayList<String> members = new ArrayList<>();
        members.add(p.getUniqueId() + "");

        if(config.getStringList("states") != null)
        {
            List list = config.getStringList("states");
            list.add(name);
            config.set("states", list);
        }
        else
        {
            config.set("states", Arrays.asList(name));
        }

        config.set("state." + name + ".king", p.getUniqueId() + "");
        config.set("state." + name + ".world", p.getWorld().getName());
        config.set("state." + name + ".location.x", loc.getBlockX());
        config.set("state." + name + ".location.y", loc.getBlockY());
        config.set("state." + name + ".location.z", loc.getBlockZ());
        config.set("state." + name + ".color", ChatColor.WHITE.getChar());
        config.set("state." + name + ".maxHealth", 5);
        config.set("state." + name + ".health", 5);

        getState(name).addMember(p);

        for(Player player : p.getWorld().getPlayers())
        {
            player.sendMessage(alert + getPlayerChatName(p) + "님이 " + name + "국가를 건설했습니다");
        }
    }

    public static State getState(Player p)
    {
        for(State state : getStates())
        {
            if(state.getMembers().contains(p))
            {
                return state;
            }
        }

        return null;
    }

    public static State getState(String name)
    {
        return new State(name);
    }

    public static State getState(Location loc)
    {
        for(State state : getStates())
        {
            if(getStateBlockLocation(state).equals(loc))
            {
                return state;
            }
        }

        return null;
    }

    public static List<State> getStates()
    {
        List<State> states = new ArrayList<State>();

        for(String name : config.getStringList("states"))
        {
            states.add(getState(name));
        }

        return states;
    }

    public static Location getStateBlockLocation(State state)
    {
        String name = state.getName();

        World w = Bukkit.getWorld(config.getString("state." + name + ".world"));
        Double x = config.getDouble("state." + name + ".location.x");
        Double y = config.getDouble("state." + name + ".location.y");
        Double z = config.getDouble("state." + name + ".location.z");

        return new Location(w, x, y, z);
    }

    public static boolean canBreakStateBlock(Block b)
    {
        if(b.getType().equals(Material.IRON_BLOCK))
        {
            for(State state : getStates())
            {
                if(b.getLocation().equals(state.getLocation().clone().add(-1, -1, -1))
                        || b.getLocation().equals(state.getLocation().clone().add(-1, -1, 0))
                        || b.getLocation().equals(state.getLocation().clone().add(-1, -1, 1))
                        || b.getLocation().equals(state.getLocation().clone().add(0, -1, -1))
                        || b.getLocation().equals(state.getLocation().clone().add(0, -1, 0))
                        || b.getLocation().equals(state.getLocation().clone().add(0, -1, 1))
                        || b.getLocation().equals(state.getLocation().clone().add(1, -1, -1))
                        || b.getLocation().equals(state.getLocation().clone().add(1, -1, 0))
                        || b.getLocation().equals(state.getLocation().clone().add(1, -1, 1)))
                {
                    return false;
                }
            }
        }
        else if(b.getType().equals(Material.GLASS_PANE))
        {
            for(State state : getStates())
            {
                for(double d=state.getLocation().getY()+1; d<=320; d++)
                {
                    Location location = state.getLocation().clone();
                    location.setY(d);

                    if(b.getLocation().equals(location))
                    {
                        return false;
                    }
                }
            }
        }
        else if(b.getType().equals(Material.BEACON))
        {
            for(State state : getStates())
            {
                if(b.getLocation().equals(state.getLocation()))
                {
                    return false;
                }
            }
        }
        else if(b.getBlockData() instanceof Bed || b.getType() == Material.CHEST)
        {
            for(State state : getStates())
            {
                if(state.isInState(b.getLocation()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean canBreakZeroBlock(Block b)
    {
        if(b.getLocation().getWorld().getName().endsWith("_nether"))
        {
            return true;
        }

        Location loc2 = b.getLocation().clone();
        loc2.setX(0); loc2.setZ(0);

        if(b.getLocation().distance(loc2) <= 100)
        {
            return false;
        }

        return true;
    }
}
