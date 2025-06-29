package org.breakthebot.townyPacts.pact;

import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.entity.Player;

import java.util.UUID;

public class pactObject {

    private UUID ID;
    private String name;
    private String nation1;
    private String nation2;
    private int duration;
    private long createdAt;
    private long expiresAt;
    private UUID createdBy;// leader of nation1; NAP request sender
    private UUID acceptedBy; // leader of nation2


    public pactObject(String name, String nation1, String nation2, int duration, UUID createdBy, UUID acceptedBy) {
        this.ID = UUID.randomUUID();
        this.name = name;
        this.nation1 = nation1;
        this.nation2 = nation2;
        this.duration = duration;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = (duration == -1)
                ? -1L
                : this.createdAt + (duration * 86400_000L);
        this.createdBy = createdBy;
        this.acceptedBy = acceptedBy;
    }

    public pactObject() {}

    public UUID getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getNation1() {
        return nation1;
    }

    public String getNation2() {
        return nation2;
    }

    public int getDuration() {
        return duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public UUID getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(UUID acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() > expiresAt;
    }

    public boolean involves(String nationName) {
        return nation1.equalsIgnoreCase(nationName) || nation2.equalsIgnoreCase(nationName);
    }

    public String getTargetNation(String ownNation) {
        if (nation1.equalsIgnoreCase(ownNation)) return nation2;
        if (nation2.equalsIgnoreCase(ownNation)) return nation1;
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof pactObject)) return false;
        pactObject other = (pactObject) obj;
        return ID.equals(other.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public String toString() {
        return "Pact{name='%s', nations='%s <-> %s', duration=%d, createdBy=%s}"
                .formatted(name, nation1, nation2, duration, createdBy);
    }
}
