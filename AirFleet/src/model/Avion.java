package model;

public class Avion {
    private int id;
    private String fabricant;
    private String modele;
    private int capacite;
    private int autonomie;
    private int crashs;
    private int anneeService;

    public Avion(int id, String fabricant, String modele, int capacite,
                 int autonomie, int crashs, int anneeService) {
        this.id = id;
        this.fabricant = fabricant;
        this.modele = modele;
        this.capacite = capacite;
        this.autonomie = autonomie;
        this.crashs = crashs;
        this.anneeService = anneeService;
    }

    // Getters
    public int getId() { return id; }
    public String getFabricant() { return fabricant; }
    public String getModele() { return modele; }
    public int getCapacite() { return capacite; }
    public int getAutonomie() { return autonomie; }
    public int getCrashs() { return crashs; }
    public int getAnneeService() { return anneeService; }

    @Override
    public String toString() {
        return id + " | " + fabricant + " " + modele +
               " | Capacité: " + capacite +
               " | Autonomie: " + autonomie +
               " | Crashs: " + crashs +
               " | Service: " + anneeService;
    }
}
