package me.channy7123.zero;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.channy7123.zero.UI.BeaconUI.updateBeaconUI;
import static me.channy7123.zero.manager.ChatManager.setPlayerNickname;
import static me.channy7123.zero.manager.ChatManager.setPlayerNicknameColor;
import static me.channy7123.zero.manager.StateManager.*;
import static me.channy7123.zero.manager.WarManager.inWar;
import static me.channy7123.zero.manager.WarManager.war;
import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.Zero.config;

public class State
{
    String name;
    Location loc;
    Player king;
    ChatColor color;
    ArrayList<Player> members = new ArrayList<Player>();
    int maxHealth;
    int health;

    public State(String name)
    {
        this.name = name;

        this.loc = new Location(Bukkit.getWorld(config.getString("state." + name + ".world")),
                config.getDouble("state." + name + ".location.x"),
                config.getDouble("state." + name + ".location.y"),
                config.getDouble("state." + name + ".location.z"));
        this.king = Bukkit.getPlayer(UUID.fromString(config.getString("state." + name + ".king")));
        this.color = ChatColor.getByChar(config.getString("state." + name + ".color"));
        this.maxHealth = config.getInt("state." + name + ".maxHealth");
        this.health = config.getInt("state." + name + ".health");

        for(String uuid : config.getStringList("state." + name + ".member"))
        {
            members.add(Bukkit.getPlayer(UUID.fromString(uuid)));
        }
    }

    public Location getLocation()
    {
        return loc;
    }

    public Player getKing()

    {
        return king;
    }

    public OfflinePlayer getOfflineKing()
    {
        return Bukkit.getOfflinePlayer(UUID.fromString(config.getString("state." + name + ".king")));
    }

    public ChatColor getColor()
    {
        return color;
    }

    public ArrayList<Player> getMembers()
    {
        members.clear();

        for(String uuid : config.getStringList("state." + name + ".member"))
        {
            try
            {
                members.add(Bukkit.getPlayer(UUID.fromString(uuid)));
            }
            catch (Exception e)
            {
                members.add((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
            }
        }

        return members;
    }

    public String getName()
    {
        return name;
    }

    public int getMaxHealth() { return config.getInt("state." + name + ".maxHealth"); }

    public int getHealth() { return config.getInt("state." + name + ".health"); }

    public boolean equal(State state)
    {
        if(state == null)
        {
            return false;
        }

        if(getName().equals(state.getName()) && getLocation().equals(state.getLocation()))
        {
            return true;
        }

        return false;
    }

    public State addMember(Player p)
    {
        if(!members.contains(p))
        {
            members.add(p);
        }

        setPlayerNickname(p, getName());
        setPlayerNicknameColor(p, color);

        ArrayList<String> configMember = new ArrayList<String>();

        for(String uuid : config.getStringList("state." + name + ".member"))
        {
            configMember.add(uuid);
        }

        configMember.add(p.getUniqueId() + "");

        config.set("state." + getName() + ".member", configMember);

        return this;
    }

    public State removeMember(Player p)
    {
        if(members.contains(p))
        {
            members.remove(p);
        }

        setPlayerNickname(p, null);
        setPlayerNicknameColor(p, ChatColor.WHITE);

        ArrayList<String> configMember = new ArrayList<String>();

        for(String uuid : config.getStringList("state." + name + ".member"))
        {
            configMember.add(uuid);
        }

        configMember.remove(p.getUniqueId() + "");

        config.set("state." + getName() + ".member", configMember);

        return this;
    }

    public State setColor(ChatColor color)
    {
        this.color = color;
        config.set("state." + name + ".color", color.getChar());

        return this;
    }

    public State setMaxHealth(int i)
    {
        maxHealth = i;
        config.set("state." + name + ".maxHealth", i);

        return this;
    }

    public State setHealth(int i)
    {
        health = i;
        config.set("state." + name + ".health", i);

        return this;
    }

    public boolean isInState(Location loc)
    {
        if(loc.getX() < getLocation().getX() + 75 && loc.getX() > getLocation().getX() - 75)
        {
            if(loc.getZ() < getLocation().getZ() + 75 && loc.getZ() > getLocation().getZ() - 75)
            {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getWarStates()
    {
        ArrayList<String> states = new ArrayList<String>();

        if(inWar.contains(this.getName()))
        {
            if(war.containsKey(this.getName()))
            {
                for(String s : war.get(this.getName()))
                {
                    states.add(s);
                }
            }

            for(String name : war.keySet())
            {
                State state = getState(name);

                for(String s : war.get(state.getName()))
                {
                    State state2 = getState(s);

                    if(state2.equal(this))
                    {
                        states.add(state.getName());
                    }
                }
            }
        }

        return states;
    }

    public boolean canAccess(Player p)
    {
        if(notinState(p))
        {
            return false;
        }

        State state = getState(p);

        if(inWar.contains(this.getName()))
        {
            if(getWarStates().contains(state.getName()))
            {
                return true;
            }
        }

        if(this.equal(state))
        {
            return true;
        }

        return false;
    }

    public void attackBeacon(Player p, int i)
    {
        if(getHealth() > i)
        {
            setHealth(getHealth() - i);
        }
        else
        {
            destroy(getState(p));
        }

        updateBeaconUI(this);
    }

    public void healBeacon(Player p, int i)
    {
        if(getMaxHealth() < getHealth() + i)
        {
            setHealth(getMaxHealth());
        }
        else
        {
            setHealth(getHealth() + i);
        }

        updateBeaconUI(this);
    }

    public void declareWar(State state)
    {
        if(!inWar.contains(this.getName()))
        {
            inWar.add(this.getName());
        }
        if(!inWar.contains(state.getName()))
        {
            inWar.add(state.getName());
        }

        if(war.containsKey(this.getName()))
        {
            ArrayList<String> states = new ArrayList<String>();

            for(String s : war.get(this.getName()))
            {
                states.add(s);
            }

            states.add(state.getName());

            war.put(this.getName(), states);
        }
        else
        {
            ArrayList<String> states = new ArrayList<String>();
            states.add(state.getName());

            war.put(this.getName(), states);
        }

        Bukkit.broadcastMessage(alert + this.getName() + "국가가 " + state.getName() + "국가에 선전포고를 했습니다");

        for(Player player : state.getMembers())
        {
            if(player != null)
            {
                player.sendTitle(ChatColor.YELLOW + this.getName() + ChatColor.RESET + "국가가 " + ChatColor.YELLOW + state.getName() + ChatColor.RESET + "국가에 선전포고를 했습니다", "", 10, 40, 10);
            }
        }
        for(Player player : this.getMembers())
        {
            if(player != null)
            {
                player.sendTitle(ChatColor.YELLOW + this.getName() + ChatColor.RESET + "국가가 " + ChatColor.YELLOW + state.getName() + ChatColor.RESET + "국가에 선전포고를 했습니다", "", 10, 40, 10);
            }
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(getWarStates().contains(state))
                {
                    declareTruce(state);
                }
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Zero"), 20*60*90);
    }

    public void declareTruce(State state)
    {
        ArrayList<String> inWarClone = (ArrayList<String>) inWar.clone();
        HashMap<String, ArrayList<String>> warClone = (HashMap<String, ArrayList<String>>) war.clone();

        if(state.getWarStates().size() == 1)
        {
            inWarClone.remove(state.getName());
        }

        if(warClone.get(state.getName()) != null)
        {
            ArrayList<String> warClone2 = warClone.get(state.getName());
            warClone2.remove(this.getName());
            warClone.put(state.getName(), warClone2);
        }

        if(warClone.containsKey(this.getName()))
        {
            ArrayList<String> warClone2 = warClone.get(this.getName());
            warClone2.remove(state.getName());
            warClone.put(state.getName(), warClone2);
        }

        inWar = inWarClone;
        war = warClone;

        Bukkit.broadcastMessage(alert + ChatColor.YELLOW + this.getName() + ChatColor.RESET + "국가가 " + ChatColor.YELLOW + state.getName() + ChatColor.RESET + "국가에 휴전을 선언했습니다");

        for(Player player : state.getMembers())
        {
            if(player != null)
            {
                player.sendTitle(ChatColor.YELLOW + this.getName() + ChatColor.RESET + "국가가 " + ChatColor.YELLOW + state.getName() + ChatColor.RESET + "국가에 휴전을 선언했습니다", "", 10, 40, 10);
            }
        }
        for(Player player : this.getMembers())
        {
            if (player != null)
            {
                player.sendTitle(ChatColor.YELLOW + this.getName() + ChatColor.RESET + "국가가 " + ChatColor.YELLOW + state.getName() + ChatColor.RESET + "국가에 휴전을 선언했습니다", "", 10, 40, 10);
            }
        }
    }

    public void destroy(State state)
    {
        if(state == null)
        {
            ArrayList<String> inWarClone = (ArrayList<String>) inWar.clone();
            HashMap<String, ArrayList<String>> warClone = (HashMap<String, ArrayList<String>>) war.clone();

            inWarClone.remove(this.getName());

            if(this.getWarStates().size() != 0)
            {
                for(String name : this.getWarStates())
                {
                    State state2 = getState(name);

                    if(state2.getWarStates().size() == 1)
                    {
                        inWarClone.remove(state2.getName());
                    }

                    if(warClone.get(state2.getName()) != null)
                    {
                        ArrayList<String> warClone2 = warClone.get(state2.getName());
                        warClone2.remove(this.getName());

                        warClone.put(state2.getName(), warClone2);
                    }
                }

                if(warClone.containsKey(this.getName()))
                {
                    warClone.remove(this.getName());
                }
            }

            inWar = inWarClone;
            war = warClone;

            Bukkit.broadcastMessage(alert + this.getName() + "국가가 자멸했습니다");

            getLocation().getBlock().setType(Material.OBSIDIAN);

            loc.clone().add(-1, -1, -1).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(-1, -1, 0).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(-1, -1, 1).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(0, -1, -1).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(0, -1, 0).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(0, -1, 1).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(1, -1, -1).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(1, -1, 0).getBlock().setType(Material.COBBLESTONE);
            loc.clone().add(1, -1, 1).getBlock().setType(Material.COBBLESTONE);

            for(double d=loc.getY()+1; d<=320; d++)
            {
                Location location = loc.clone();
                location.setY(d);
                location.getBlock().setType(Material.AIR);
            }

            for(String uuid : config.getStringList("state." + name + ".member"))
            {
                UUID id = UUID.fromString(uuid);

                config.set(id + ".chatNickname", null);
                config.set(id + ".chatNicknameColor", ChatColor.WHITE.getChar());
            }

            ArrayList<String> stateList = new ArrayList<String>();
            stateList = (ArrayList<String>) config.getStringList("states");
            stateList.remove(this.name);
            config.set("states", stateList);

            config.set("state." + this.name, null);

            return;
        }

        ArrayList<String> inWarClone = (ArrayList<String>) inWar.clone();
        HashMap<String, ArrayList<String>> warClone = (HashMap<String, ArrayList<String>>) war.clone();

        inWarClone.remove(this);

        if(this.getWarStates().size() != 0)
        {
            for(String name : this.getWarStates())
            {
                State state2 = getState(name);

                if(state2.getWarStates().size() == 1)
                {
                    inWarClone.remove(state2.getName());
                }

                if(warClone.get(state2.getName()) != null)
                {
                    if(warClone.get(state2.getName()).size() != 0)
                    {
                        ArrayList<String> warClone2 = warClone.get(state2.getName());
                        warClone2.remove(this.getName());

                        warClone.put(state2.getName(), warClone2);
                    }
                }
            }

            if(warClone.containsKey(this.getName()))
            {
                warClone.remove(this.getName());
            }
        }

        inWar = inWarClone;
        war = warClone;

        for(Player player : this.getMembers())
        {
            if(player.isOnline())
            {
                player.sendTitle(this.getName() + "국가가 " + state.getName() + "국가에 의해 멸망했습니다", "", 10, 40, 10);
            }
        }
        for(Player player : state.getMembers())
        {
            if(player.isOnline())
            {
                player.sendTitle(this.getName() + "국가가 " + state.getName() + "국가에 의해 멸망했습니다", "", 10, 40, 10);
            }
        }

        Bukkit.broadcastMessage(alert + this.getName() + "국가가 " + state.getName() + "국가에 의해 멸망했습니다");

        getLocation().getBlock().setType(Material.OBSIDIAN);

        loc.clone().add(-1, -1, -1).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(-1, -1, 0).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(-1, -1, 1).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(0, -1, -1).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(0, -1, 0).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(0, -1, 1).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(1, -1, -1).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(1, -1, 0).getBlock().setType(Material.COBBLESTONE);
        loc.clone().add(1, -1, 1).getBlock().setType(Material.COBBLESTONE);

        for(double d=loc.getY()+1; d<=320; d++)
        {
            Location location = loc.clone();
            location.setY(d);
            location.getBlock().setType(Material.AIR);
        }

        for(String uuid : config.getStringList("state." + name + ".member"))
        {
            UUID id = UUID.fromString(uuid);

            config.set(id + ".chatNickname", null);
            config.set(id + ".chatNicknameColor", ChatColor.WHITE.getChar());
        }

        ArrayList<String> stateList = new ArrayList<String>();
        stateList = (ArrayList<String>) config.getStringList("states");
        stateList.remove(this.name);
        config.set("states", stateList);

        config.set("state." + this.name, null);
    }
}
