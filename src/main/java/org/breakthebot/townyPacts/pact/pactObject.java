package org.breakthebot.townyPacts.pact;

import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.entity.Player;

import java.util.UUID;

public class pactObject {

    private UUID ID;
    private String name;
    private Nation nation1;
    private Nation nation2;
    private int duration;
    private long createdAt;
    private long expiresAt;
    private Player createdBy; // leader of nation1; NAP request sender
    private Player acceptedBy; // leader of nation2


    public void Pact(String name, Nation nation1, Nation nation2, int duration, Player createdBy, Player acceptedBy) {
        this.ID = UUID.randomUUID();
        this.name = name;
        this.nation1 = nation1;
        this.nation2 = nation2;
        this.duration = duration;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = System.currentTimeMillis() * (duration * 86400L);
        this.createdBy = createdBy;
        this.acceptedBy = acceptedBy;
    }
}
