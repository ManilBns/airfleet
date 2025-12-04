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
    public void setId(int id) {
		this.id = id;
	}

	public void setFabricant(String fabricant) {
		this.fabricant = fabricant;
	}

	public void setModele(String modele) {
		this.modele = modele;
	}

	public void setCapacite(int capacite) {
		this.capacite = capacite;
	}

	public void setAutonomie(int autonomie) {
		this.autonomie = autonomie;
	}

	public void setCrashs(int crashs) {
		this.crashs = crashs;
	}

	public void setAnneeService(int anneeService) {
		this.anneeService = anneeService;
	}

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
