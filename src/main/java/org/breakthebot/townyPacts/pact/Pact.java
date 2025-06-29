package org.breakthebot.townyPacts.pact;

import org.breakthebot.townyPacts.TownyPacts;

import java.util.UUID;

public class Pact {

    private UUID PactID;
    private String name;
    private String nation1;
    private String nation2;
    private int duration;
    private long createdAt;
    private long expiresAt;
    private UUID sentBy;// leader of nation1; pact request sender
    private UUID acceptedBy; // leader of nation2
    private String status;

    public Pact(String name, String nation1, String nation2, int duration, UUID sentBy, UUID acceptedBy) {
        this.PactID = UUID.randomUUID();
        this.name = name;
        this.nation1 = nation1;
        this.nation2 = nation2;
        this.duration = duration;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = (duration == -1)
                ? -1L
                : this.createdAt + (duration * 86400_000L);
        this.sentBy = sentBy;
        this.acceptedBy = acceptedBy;
        this.status = "PENDING";
    }

    public Pact() {}

    public UUID getPactID() { return PactID; }

    public String getName() { return name; }

    public String getNation1() { return nation1; }

    public String getNation2() { return nation2; }

    public int getDuration() { return duration; }

    public long getCreatedAt() { return createdAt; }

    public long getExpiresAt() { return expiresAt; }

    public UUID getSentBy() { return sentBy; }

    public UUID getAcceptedBy() { return acceptedBy; }

    public void setAcceptedBy(UUID acceptedBy) {
        this.acceptedBy = acceptedBy;
        setStatus("ACTIVE");
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

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

    public void breakPact(String ownNation, String targetNation) {
        this.expiresAt = System.currentTimeMillis() + (TownyPacts.getInstance().getConfiguration().breakCooldownHours * 3600L * 1000);
        this.status = "BROKEN";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pact)) return false;
        Pact other = (Pact) obj;
        return PactID.equals(other.PactID);
    }

    @Override
    public int hashCode() {
        return PactID.hashCode();
    }

    @Override
    public String toString() {
        return "Pact{name='%s', nations='%s <-> %s', duration=%d, createdBy=%s}"
                .formatted(name, nation1, nation2, duration, sentBy);
    }

}
