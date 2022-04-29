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

package com.andrei1058.bedwars.commands.map;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class MapCommand extends BukkitCommand {

    public MapCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender s, String st, String[] args, String[] array) {
        if (s instanceof ConsoleCommandSender) {
            commandSender.sendMessage("This command is for players!");
            return true;
        } else {
            Player player = (Player)commandSender;
            IArena arena = Arena.getArenaByPlayer(player);
            if (arena == null) {
                return false;
            } else if (arena.getStatus() != GameState.waiting && arena.getStatus() != GameState.starting && arena.getStatus() != GameState.playing) {
                if (arena.getStatus() != GameState.waiting || arena.getStatus() != GameState.starting || arena.getStatus() != GameState.playing) {
                    player.sendMessage(ChatColor.RED + "You are currently not playing anything!");
                }

                return false;
            } else {
                player.sendMessage(ChatColor.GREEN + "You are currently playing on " + ChatColor.YELLOW + arena.getDisplayName());
                return true;
            }
        }
    }
}
