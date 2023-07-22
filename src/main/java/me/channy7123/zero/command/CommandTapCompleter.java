package me.channy7123.zero.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandTapCompleter implements TabCompleter
{

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> commands = new ArrayList<String>();

        if(command.getName().equalsIgnoreCase("zero"))
        {
            if(args.length == 1)
            {
                if(sender.isOp())
                {
                    commands.add("setting");
                }

                commands.add("set");
                commands.add("recall");
            }
            else if(args.length == 2)
            {
                if(args[0].equalsIgnoreCase("set"))
                {
                    commands.add("name");
                }
            }
        }

        if(command.getName().equalsIgnoreCase("state"))
        {
            if(args.length == 1)
            {
                commands.add("recall");
                commands.add("quit");
                commands.add("info");
                commands.add("destroy");
            }
        }

        return commands;
    }
}
