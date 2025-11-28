package model;

import java.sql.Date;

public class Crash {
    private int id;
    private int avionId;
    private String modele;
    private Date dateCrash;
    private String lieu;
    private String gravite;
    private int morts;
    private int blesses;
    private String cause;
    private String description;
    public Crash(int id, int avionId, String modele, Date date, String lieu, String gravite,
                 int morts, int blesses, String cause, String description) {
        this.id = id;
        this.avionId = avionId;
        this.modele = modele;
        this.dateCrash = date;
        this.lieu = lieu;
        this.gravite = gravite;
        this.morts = morts;
        this.blesses = blesses;
        this.cause = cause;
        this.description = description;
    }

    // getters (et setters si besoin)
    public int getId() { return id; }
    public int getAvionId() { return avionId; }
    public Date getDateCrash() { return dateCrash; }
    public String getLieu() { return lieu; }
    public String getGravite() { return gravite; }
    public String getModele() { return modele; }
    public int getMorts() { return morts; }
    public int getBlesses() { return blesses; }
    public String getCause() { return cause; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return id + " | avionId:" + avionId + " | modele:" + modele + " | date:" + dateCrash +
               " | lieu:" + lieu + " | gravite:" + gravite + " | morts:" + morts +
               " | blessés:" + blesses + " | cause:" + cause +
               (description != null ? " | " + description : "");
    }
}
