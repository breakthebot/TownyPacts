package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;



public class Overclaim implements Listener {


    @EventHandler
    public void onPreClaim(TownPreClaimEvent event) {
        if (!event.isOverClaim()) { return; }

        Town attackingTown = event.getTown();
        if (attackingTown == null) { return; }

        WorldCoord coord = event.getTownBlock().getWorldCoord();
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(coord);
        if (townBlock == null || !townBlock.hasTown()) { return; }

        Town defendingTown = townBlock.getTownOrNull();
        if (defendingTown == null || defendingTown.equals(attackingTown)) { return; }

        Nation attackerNation = attackingTown.getNationOrNull();
        Nation defenderNation = defendingTown.getNationOrNull();
        if (attackerNation == null || defenderNation == null) { return; }

        if (MetaData.hasActivePact(attackerNation, defenderNation)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            TownyMessaging.sendErrorMsg(player, "You cannot overclaim this town since your nations have an active pact.");
        }
    }
}