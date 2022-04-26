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

package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunniListener implements Listener {

    // Damage resistance on game end
    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        for (UUID p : e.getWinners ()) {
            Player player = Bukkit.getPlayer ( p );
            if (player == null) return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 240, 1));
        }
    }

    // Disable drop while mid air
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            if (BedWars.getAPI().getArenaUtil().isPlaying(player)) {
                if (BedWars.getAPI().getArenaUtil().getArenaByPlayer(player).getStatus().equals(GameState.playing)) {
                    List<Block> blocks = new ArrayList<>();
                    blocks.add(player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock());

                    for(int i = 1; i <= 4; ++i) {
                        blocks.add(player.getLocation().clone().subtract(0.0D, (double)i, 0.0D).getBlock());
                    }

                    if (!blocks.stream().anyMatch((b) -> {
                        return !b.getType().equals(Material.AIR);
                    })) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    // Deprecated
    /*
     * @EventHandler
     *     public void onItemDrop(PlayerDropItemEvent e) {
     *         if (e.isCancelled()) return;
     *         if (BedWars.getServerType() == ServerType.MULTIARENA) {
     *             if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
     *                 e.setCancelled(true);
     *                 return;
     *             }
     *         }
     *         Player player = e.getPlayer();
     *         Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
     *         if (!BedWars.getAPI().getArenaUtil().isPlaying(player)) return;
     *         if (!BedWars.getAPI().getArenaUtil().getArenaByPlayer(player).getStatus().equals(GameState.playing)) return;
     *         if (!block.getType().equals(Material.AIR)) return;
     *         e.setCancelled(true);
     *     }
     */
}
