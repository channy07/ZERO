package me.channy7123.zero;

import me.channy7123.zero.UI.*;
import me.channy7123.zero.command.CommandState;
import me.channy7123.zero.command.CommandTapCompleter;
import me.channy7123.zero.command.CommandZero;
import me.channy7123.zero.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Zero extends JavaPlugin
{
    public static FileConfiguration config = Bukkit.getServer().spigot().getConfig();
    public static String alert = "[" + ChatColor.LIGHT_PURPLE + "ZER0" + ChatColor.RESET + "] ";

    @Override
    public void onEnable()
    {
        System.out.println("----------------------------------------------");
        System.out.println("  P r o j e c t   Z E R 0 ");
        System.out.println(" bug report : channy070123@gmail.com ");
        System.out.println("----------------------------------------------");

        this.saveDefaultConfig();
        config = this.getConfig();

        this.getCommand("zero").setExecutor(new CommandZero());
        this.getCommand("state").setExecutor(new CommandState());
        this.getCommand("zero").setTabCompleter(new CommandTapCompleter());
        this.getCommand("state").setTabCompleter(new CommandTapCompleter());
        getServer().getPluginManager().registerEvents(new ChatManager(), this);
        getServer().getPluginManager().registerEvents(new StateManager(), this);
        getServer().getPluginManager().registerEvents(new BeaconUI(), this);
        getServer().getPluginManager().registerEvents(new SettingUI(), this);
        getServer().getPluginManager().registerEvents(new Setting(), this);
        getServer().getPluginManager().registerEvents(new PlayerMenuManager(), this);
        getServer().getPluginManager().registerEvents(new ZeroShop(), this);
        getServer().getPluginManager().registerEvents(new WarManager(), this);
        getServer().getPluginManager().registerEvents(new AttackItemUI(), this);
        getServer().getPluginManager().registerEvents(new RecallManager(), this);
        getServer().getPluginManager().registerEvents(new TruceUI(), this);
        getServer().getPluginManager().registerEvents(new BlockRemover(), this);
    }

    @Override
    public void onDisable()
    {
        System.out.println("----------------------------------------------");
        System.out.println("  P r o j e c t   Z E R 0 ");
        System.out.println(" bug report : channy070123@gmail.com");
        System.out.println("----------------------------------------------");

        config.options().copyDefaults(true);
        saveConfig();
    }
}
