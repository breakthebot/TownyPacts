/*
 * This file is part of TownyAddons.
 *
 * TownyAddons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TownyAddons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TownyAddons. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.event.NationPreAddEnemyEvent;
import com.palmergames.bukkit.towny.event.NationRemoveAllyEvent;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NationRelationListener implements Listener {

    @EventHandler
    public void onNationEnemy(NationPreAddEnemyEvent event) {
        boolean hasPact = MetaData.hasActivePact(event.getNation(), event.getEnemy());
        if (hasPact) {
            event.setCancelled(true);
            event.setCancelMessage("You cannot enemy the nation of " + event.getEnemyName() + " as you have an active pact.");
        }
    }

    @EventHandler
    public void onNationUnAlly(NationRemoveAllyEvent event) {
        boolean required = TownyPacts.getInstance().getConfiguration().pactRequireAlly;
        if (required && MetaData.hasActivePact(event.getNation(), event.getRemovedNation())) {
            event.setCancelled(true);
            event.setCancelMessage("You cannot unally a nation you have an active pact with!");
        }
    }
}
