/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.andrei1058.bedwars.commands.bedwars.subcmds.sensitive;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.command.ParentCommand;
import com.andrei1058.bedwars.api.command.SubCommand;
import com.andrei1058.bedwars.api.events.player.PlayerXpGainEvent;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.Misc;
import com.andrei1058.bedwars.arena.SetupSession;
import com.andrei1058.bedwars.configuration.LevelsConfig;
import com.andrei1058.bedwars.configuration.Permissions;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Level extends SubCommand {

    public Level(ParentCommand parent, String name) {
        super(parent, name);
        setPermission(Permissions.PERMISSION_LEVEL);
        setPriority(10);
        showInList(true);
        setDisplayInfo(Misc.msgHoverClick("§8- §e/" + getParent().getName() + " " + getSubCommandName() + " §8      - §eClick for details", "§eManage a player level.",
                "/" + getParent().getName() + " " + getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (args.length == 0) {
            sendSubCommands(s, BedWars.getAPI());
            return true;
        }
        if (args[0].equalsIgnoreCase("setlevel")) {
            if (args.length != 3) {
                s.sendMessage(ChatColor.RED + "Usage: /bw level setLevel <player> <level>");
                return true;
            }
            Player pl = Bukkit.getPlayer(args[1]);
            if (pl == null) {
                s.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }

            int level;

            try {
                level = Integer.parseInt(args[2]);
            } catch (Exception e) {
                s.sendMessage(ChatColor.RED + "Level must be a number!");
                return true;
            }

            BedWars.getAPI().getLevelsUtil().setLevel(pl, level);

            int nextLevelCost =  LevelsConfig.levels.getYml().get("levels." + level + ".rankup-cost") == null ?
                    LevelsConfig.levels.getInt("levels.others.rankup-cost") : LevelsConfig.levels.getInt("levels." + level + ".rankup-cost");

            String levelName = LevelsConfig.levels.getYml().get("levels." + level + ".name") == null ?
                    LevelsConfig.levels.getYml().getString("levels.others.name") : LevelsConfig.levels.getYml().getString("levels." + level + ".name");


            BedWars.plugin.getServer().getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
                BedWars.getRemoteDatabase().setLevelData(pl.getUniqueId(), level, 0, levelName, nextLevelCost);
                s.sendMessage(ChatColor.GREEN + "Player" + pl.getName() + " level was set to: " + args[2]);
                s.sendMessage(ChatColor.RED + ChatColor.BOLD + "NOTE: " + ChatColor.RED + "The player may need to rejoin to see it updated.");
            });
        } else if (args[0].equalsIgnoreCase("givexp")) {
            if (args.length != 3) {
                s.sendMessage(ChatColor.RED + "Usage: /bw level giveXp <player> <amount>");
                return true;
            }
            Player pl = Bukkit.getPlayer(args[1]);
            if (pl == null) {
                s.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (Exception e) {
                s.sendMessage(ChatColor.RED + "Amount must be a number!");
                return true;
            }

            BedWars.getAPI().getLevelsUtil().addXp(pl, amount, PlayerXpGainEvent.XpSource.OTHER);

            BedWars.plugin.getServer().getScheduler().runTaskAsynchronously(BedWars.plugin, () -> {
                Object[] data = BedWars.getRemoteDatabase().getLevelData(pl.getUniqueId());
                BedWars.getRemoteDatabase().setLevelData(pl.getUniqueId(), (Integer) data[0], ((Integer)data[1]) + amount, (String) data[2], (Integer)data[3]);
                s.sendMessage(ChatColor.GREEN + args[2] + " xp was given to " + pl.getName());
                s.sendMessage(ChatColor.RED + ChatColor.BOLD + "NOTE: " + ChatColor.RED + "The player may need to rejoin to see it updated.");
            });
        } else {
            sendSubCommands(s, BedWars.getAPI());
        }
        return true;
    }

    private void sendSubCommands(CommandSender s, com.andrei1058.bedwars.api.BedWars api) {
        if (s instanceof Player) {
            Player p = (Player) s;
            p.spigot().sendMessage(Misc.msgHoverClick("§8- §e/" + getParent().getName() + " " + getSubCommandName() + " setLevel <player> <level>",
                    "§eSet a player level.", "/" + getParent().getName() + " " + getSubCommandName() + " setLevel",
                    ClickEvent.Action.SUGGEST_COMMAND));
            p.spigot().sendMessage(Misc.msgHoverClick("§8- §e/" + getParent().getName() + " " + getSubCommandName() + " giveXp <player> <amount>",
                    "§eGive Xp to a player.", "/" + getParent().getName() + " <player>" + getSubCommandName() + " giveXp",
                    ClickEvent.Action.SUGGEST_COMMAND));
        } else {
            s.sendMessage(ChatColor.GREEN + "bw level setLevel <player> <level>");
            s.sendMessage(ChatColor.GREEN + "bw level giveXp <player> <amount>");
        }
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    @Override
    public boolean canSee(CommandSender s, com.andrei1058.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;

        Player p = (Player) s;
        if (Arena.isInArena(p)) return false;

        if (SetupSession.isInSetupSession(p.getUniqueId())) return false;
        return hasPermission(s);
    }
}
