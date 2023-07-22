package me.channy7123.zero.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.channy7123.zero.manager.RecallManager.recallState;
import static me.channy7123.zero.manager.StateManager.getState;
import static me.channy7123.zero.Zero.alert;
import static me.channy7123.zero.manager.StateManager.notinState;

public class CommandState implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            return false;
        }

        Player p = (Player) sender;

        if(command.getName().equalsIgnoreCase("state"))
        {
            if(args.length == 0)
            {
                return false;
            }

            if(args[0].equalsIgnoreCase("destroy"))
            {
                if(getState(p) != null)
                {
                    if(getState(p).getKing().equals(p))
                    {
                        getState(p).destroy(null);
                    }
                    else
                    {
                        p.sendMessage(alert + "왕이 아닙니다");
                    }
                }
                else
                {
                    p.sendMessage(alert + "국가에 소속되어있지 않습니다");
                }
            }
            else if(args[0].equalsIgnoreCase("quit"))
            {
                if(getState(p) != null)
                {
                    if(!getState(p).getKing().equals(p))
                    {
                        p.sendMessage(alert + getState(p).getName() + "국가에서 탈퇴하였습니다");
                        getState(p).removeMember(p);
                    }
                    else
                    {
                        p.sendMessage(alert + "왕은 국가에서 탈퇴 할 수 없습니다");
                    }
                }
                else
                {
                    p.sendMessage(alert + "국가에 소속되어있지 않습니다");
                }
            }
            else if(args[0].equalsIgnoreCase("recall"))
            {
                recallState(p);
            }
            else if(args[0].equalsIgnoreCase("info"))
            {
                if(notinState(p))
                {
                    p.sendMessage(alert + "국가에 소속되어있지 않습니다");
                    return true;
                }

                p.sendMessage(alert + "이름 : " + getState(p).getName());

                try
                {
                    p.sendMessage(alert + "왕 : " + getState(p).getKing().getName());
                }
                catch (Exception e)
                {
                    p.sendMessage(alert + "왕 : " + getState(p).getOfflineKing().getName());
                }


                p.sendMessage(alert + "위치 : " + "X=" + getState(p).getLocation().getX() + " Y=" + getState(p).getLocation().getY() + " Z=" + getState(p).getLocation().getZ());
            }
        }

        return false;
    }
}
