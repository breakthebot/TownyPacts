package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import org.breakthebot.townyPacts.object.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.List;

public class PactCleanupTask implements Runnable {
    @Override
    public void run() {
        for (Nation nation : TownyAPI.getInstance().getNations()) {
            List<Pact> pacts = MetaData.getActivePacts(nation);
            Iterator<Pact> iter = pacts.iterator();

            long now = System.currentTimeMillis();
            boolean changed = false;

            while (iter.hasNext()) {
                Pact pact = iter.next();
                if (pact.getExpiresAt() != -1 && pact.getExpiresAt() < now) {
                    iter.remove();
                    changed = true;
                }
            }

            if (changed) {
                MetaData.saveActivePacts(nation, pacts);
            }
        }
    }
}