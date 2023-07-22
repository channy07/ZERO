package me.channy7123.zero.command;

import me.channy7123.zero.enums.StateItem;
import me.channy7123.zero.manager.ChatManager;
import me.channy7123.zero.manager.PlayerMenuManager;
import me.channy7123.zero.State;
import me.channy7123.zero.UI.SettingUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.channy7123.zero.manager.ChatManager.getPlayerChatName;
import static me.channy7123.zero.manager.RecallManager.recallZero;
import static me.channy7123.zero.manager.StateManager.*;
import static me.channy7123.zero.Zero.alert;

public class CommandZero implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
             return false;
        }

        Player p = (Player) sender;

        if(command.getName().equalsIgnoreCase("zero"))
        {
            if(args.length == 0)
            {
                return false;
            }

            if(p.isOp())
            {
                if(args[0].equalsIgnoreCase("setting"))
                {
                    p.openInventory(SettingUI.settingUI());
                }
            }

            if(args[0].equalsIgnoreCase("set"))
            {
                if(args.length == 3)
                {
                    if(args[1].equalsIgnoreCase("name"))
                    {
                        if(args[2].length() >= 10)
                        {
                            p.sendMessage(alert + "이름은 10자 미만이어야 합니다");
                            return false;
                        }
                        ChatManager.setPlayerName(p, args[2]);
                        p.sendMessage(alert + "이름이 " + args[2] + "로 설정되었습니다");
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("recall"))
            {
                recallZero(p);
            }
            else if(args[0].equalsIgnoreCase("join"))
            {
                if(PlayerMenuManager.invitePlayer.containsKey(p))
                {
                    Player player = PlayerMenuManager.invitePlayer.get(p);

                    getState(player).addMember(p);
                    p.sendMessage(alert + getState(player).getName() + " 국가에 소속되었습니다");
                    player.sendMessage(alert + getPlayerChatName(p) + "님을 국가에 초대했습니다");

                    PlayerMenuManager.invitePlayer.remove(p);
                }
            }
            else if(args[0].equalsIgnoreCase("nojoin"))
            {
                if(PlayerMenuManager.invitePlayer.containsKey(p))
                {
                    Player player = PlayerMenuManager.invitePlayer.get(p);

                    p.sendMessage(alert + getState(player).getName() + " 국가의 초대를 거절했습니다");
                    player.sendMessage(alert + getPlayerChatName(p) + "님이 초대를 거절했습니다");

                    PlayerMenuManager.invitePlayer.remove(p);
                }
            }
            else if(args[0].equalsIgnoreCase("truce"))
            {
                if(waitingForTruce.containsKey(getState(p).getName()))
                {
                    State state = getState(waitingForTruce.get(getState(p).getName())); //신청국
                    waitingForTruce.remove(getState(p).getName());

                    state.declareTruce(getState(p));
                }
            }
            else if(args[0].equalsIgnoreCase("notruce"))
            {
                if(waitingForTruce.containsKey(getState(p).getName()))
                {
                    State state = getState(waitingForTruce.get(getState(p).getName()));
                    waitingForTruce.remove(getState(p).getName());

                    p.sendMessage(alert + "휴전 신청을 거절했습니다");
                    state.getKing().sendMessage(alert + getState(p).getName() + "국가가 휴전 신청을 거절했습니다");

                    state.getKing().getInventory().addItem(StateItem.TRUCE.getUseItem());
                }
            }
        }

        return false;
    }
}
