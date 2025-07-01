package org.breakthebot.townyPacts.events;

import com.palmergames.bukkit.towny.event.RenameNationEvent;
import com.palmergames.bukkit.towny.object.Nation;
import org.breakthebot.townyPacts.object.Pact;
import org.breakthebot.townyPacts.utils.MetaData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;


public class Update implements Listener {

    @EventHandler
    public void onNationRename(RenameNationEvent event) {
        String oldName = event.getOldName();
        String newName = event.getNation().getName();
        Nation nation = event.getNation();

        List<Pact> activePacts = MetaData.getActivePacts(nation);
        for (Pact pact : activePacts) {
            if (pact.getNation1().equalsIgnoreCase(oldName)) {
                pact.setNation1(newName);
            } else if (pact.getNation2().equalsIgnoreCase(oldName)) {
                pact.setNation2(newName);
            }

            updatePactName(pact, oldName, newName);
        }

        List<Pact> pendingPacts = MetaData.getPendingPacts(nation);
        for (Pact pact : pendingPacts) {
            if (pact.getNation1().equalsIgnoreCase(oldName)) {
                pact.setNation1(newName);
            } else if (pact.getNation2().equalsIgnoreCase(oldName)) {
                pact.setNation2(newName);
            }

            updatePactName(pact, oldName, newName);
        }
    }

    private void updatePactName(Pact pact, String oldName, String newName) {
        String name = pact.getName();
        if (name.startsWith("Pact-")) {
            String[] parts = name.split("-");
            if (parts.length == 3) {
                String n1 = parts[1];
                String n2 = parts[2];

                if (n1.equalsIgnoreCase(oldName)) n1 = newName;
                if (n2.equalsIgnoreCase(oldName)) n2 = newName;

                String newPactName = "Pact-" + n1 + "-" + n2;
                pact.setName(newPactName);
            }
        }
    }
}
