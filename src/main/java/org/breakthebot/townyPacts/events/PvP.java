package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;



public class PvP implements Listener {

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Entity damager = event.getDamager();
        Player attacker = null;

        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            attacker = shooter;
        }

        if (attacker == null) return;
        TownyAPI API = TownyAPI.getInstance();
        Nation nat1 = API.getNation(victim);
        Nation nat2 = API.getNation(attacker);
        if (nat1 == null || nat2 == null) { return; }

        TownBlock block = API.getTownBlock(victim.getLocation());
        if (block == null) { return; }
        if (block.getType().getName().equalsIgnoreCase("arena")) { return; }

        if (MetaData.hasActivePact(nat1, nat2)) {
            event.setCancelled(true);
            TownyMessaging.sendErrorMsg(attacker, "You may not attack this player as your nations have an active pact");
        }
    }
}
