package service;
import java.util.List;
import java.util.Scanner;
import database.Database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Avion;
import model.Crash;
import nativeLib.NativeLib;

public class Function {
	
	static AvionService avionService = new AvionService();
    static CrashService crashService = new CrashService();
    static NativeLib nativeLib = new NativeLib();
    static Scanner sc = new Scanner(System.in);
    
    public static void tri() {	
	    String fabStat = AvionService.choisirConstructeur(avionService, sc);
	    if (fabStat != null) {
	        List<Avion> avionsFilter = avionService.searchByFabricant(fabStat);
	        if (!avionsFilter.isEmpty()) {
	            Avion[] tabFilter = avionsFilter.toArray(new Avion[0]);
	            System.out.println("Moyenne autonomie : " + nativeLib.moyenneAutonomie(tabFilter));
	            System.out.println("Moyenne de crash : " + nativeLib.moyenneCrashs(tabFilter));
	
	            int totalCrashs = crashService.countByConstructeur(fabStat);
	            int totalMorts = crashService.totalMortsParFabricant(fabStat);
	            System.out.println("Nombre total de crashs : " + totalCrashs);
	            System.out.println("Nombre total de morts : " + totalMorts);
	
	            Avion[] topFiabilite = nativeLib.trierParCrashs(tabFilter);
	            System.out.println("\nTop 3 avions les plus fiables :");
	            for (int i = 0; i < Math.min(3, topFiabilite.length); i++)
	                System.out.println((i + 1) + ". " + topFiabilite[i] +
	                        " (Crashs : " + crashService.countByAvion(topFiabilite[i].getId()) + ")");
	
	            Avion[] topAutonomie = nativeLib.trierParAutonomie(tabFilter);
	            System.out.println("\nTop 3 avions les plus autonomes :");
	            for (int i = topAutonomie.length - 1; i >= Math.max(0, topAutonomie.length - 3); i--)
	                System.out.println((topAutonomie.length - i) + ". " + topAutonomie[i]);
	
	            Avion pireAvion = topFiabilite[topFiabilite.length - 1];
	            System.out.println("\nPire avion en termes de sécurité :");
	            System.out.println("Modèle : " + pireAvion.getModele() +
	                    ", Nombre de crashs : " + crashService.countByAvion(pireAvion.getId()));
	        } 
	        else System.out.println("Aucun avion trouvé pour ce constructeur.");
	    }
	 }
    
    public static void addCrash() {
        System.out.println("\n=== AJOUT D'UN CRASH ===");
        System.out.println("Tapez 'menu' à n'importe quelle étape pour revenir au menu principal.\n");

        try {
            // ID de l'avion
            System.out.print("ID de l'avion : ");
            String avionIdStr = sc.nextLine().trim();
            if (avionIdStr.equalsIgnoreCase("menu")) return;
            int avionId = Integer.parseInt(avionIdStr);

            // Modèle : choix existant ou saisie libre
            System.out.println("Voulez-vous choisir un modèle existant ? (O/N)");
            String choixModele = sc.nextLine().trim();
            if (choixModele.equalsIgnoreCase("menu")) return;
            String crashModele;
            if (choixModele.equalsIgnoreCase("O")) {
                crashModele = AvionService.choisirModele(avionService, sc);
                if (crashModele == null) return;
            } else {
                System.out.print("Saisissez le modèle : ");
                crashModele = sc.nextLine().trim();
                if (crashModele.equalsIgnoreCase("menu")) return;
            }
            // Date du crash
            System.out.print("Date du crash (YYYY-MM-DD) : ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.equalsIgnoreCase("menu")) return;
            Date dateCrash = Date.valueOf(dateStr);
            // Lieu
            System.out.print("Lieu : ");
            String lieu = sc.nextLine().trim();
            if (lieu.equalsIgnoreCase("menu")) return;
            // Gravité
            System.out.print("Gravité : ");
            String gravite = sc.nextLine().trim();
            if (gravite.equalsIgnoreCase("menu")) return;
            // Nombre de morts
            System.out.print("Nombre de morts : ");
            String mortsStr = sc.nextLine().trim();
            if (mortsStr.equalsIgnoreCase("menu")) return;
            int morts = Integer.parseInt(mortsStr);
            // Nombre de blessés
            System.out.print("Nombre de blessés : ");
            String blessesStr = sc.nextLine().trim();
            if (blessesStr.equalsIgnoreCase("menu")) return;
            int blesses = Integer.parseInt(blessesStr);
            // Cause
            System.out.print("Cause : ");
            String cause = sc.nextLine().trim();
            if (cause.equalsIgnoreCase("menu")) return;
            // Description
            System.out.print("Description : ");
            String desc = sc.nextLine().trim();
            if (desc.equalsIgnoreCase("menu")) return;
            // Création du crash
            Crash newCrash = new Crash(0, avionId, crashModele, dateCrash, lieu, gravite, morts, blesses, cause, desc);
            crashService.addCrash(newCrash);
            System.out.println(" Crash ajouté avec succès !");
        } catch (IllegalArgumentException e) {
            System.out.println(" Format de date invalide ! Utilisez YYYY-MM-DD.");
        }
    }

    
    public static void addPlane() {
        System.out.println("\n=== AJOUT D'UN AVION ===");
        System.out.println("Tapez 'menu' à n'importe quelle étape pour revenir au menu principal.\n");

        try {
            // --- CHOIX DU CONSTRUCTEUR ---
            System.out.println("Voulez-vous choisir un constructeur existant ? (O/N)");
            String choixFab = sc.nextLine().trim();
            if (choixFab.equalsIgnoreCase("menu")) return;

            String fab;
            boolean constructeurExiste = false;

            if (choixFab.equalsIgnoreCase("O")) {
                fab = AvionService.choisirConstructeur(avionService, sc);
                if (fab == null) return; // retour menu
                constructeurExiste = true;
            } else {
                System.out.print("Saisissez le constructeur : ");
                fab = sc.nextLine().trim();
                if (fab.equalsIgnoreCase("menu")) return;

                // Vérifier si vraiment il n'existe pas déjà
                List<String> existing = avionService.getAllConstructeurs();
                constructeurExiste = existing.contains(fab);
            }

            // --- CHOIX DU MODÈLE ---
            String mod;

            if (constructeurExiste) {
                System.out.println("Voulez-vous choisir un modèle existant ? (O/N)");
                String choixModele = sc.nextLine().trim();
                if (choixModele.equalsIgnoreCase("menu")) return;

                if (choixModele.equalsIgnoreCase("O")) {
                    // Utilisation de la fonction choisirModelePourConstructeur
                    mod = AvionService.choisirModelePourConstructeur(avionService, sc, fab);
                    if (mod == null) return; // retour menu
                } else {
                    System.out.print("Saisissez le modèle : ");
                    mod = sc.nextLine().trim();
                    if (mod.equalsIgnoreCase("menu")) return;
                }

            } else {
                // Nouveau constructeur → modèle obligatoire
                System.out.println("Ce constructeur n'existe pas encore vous devez saisir un nouveau modèle.");
                System.out.print("Modèle : ");
                mod = sc.nextLine().trim();
                if (mod.equalsIgnoreCase("menu")) return;
            }

            // --- SAISIE DES AUTRES INFORMATIONS ---
            System.out.print("Capacité : ");
            String capStr = sc.nextLine().trim();
            if (capStr.equalsIgnoreCase("menu")) return;
            int cap = Integer.parseInt(capStr);

            System.out.print("Autonomie (km) : ");
            String autoStr = sc.nextLine().trim();
            if (autoStr.equalsIgnoreCase("menu")) return;
            int auto = Integer.parseInt(autoStr);

            System.out.print("Nombre de crashs : ");
            String crStr = sc.nextLine().trim();
            if (crStr.equalsIgnoreCase("menu")) return;
            int crr = Integer.parseInt(crStr);

            System.out.print("Année d'entrée en service : ");
            String yearStr = sc.nextLine().trim();
            if (yearStr.equalsIgnoreCase("menu")) return;
            int year = Integer.parseInt(yearStr);

            if (year < 1903 || year > 2025) {
                System.out.println("Année invalide ! Elle doit être comprise entre 1903 et 2025.");
                return;
            }

            // --- AJOUT EN BDD ---
            Avion newAvion = new Avion(0, fab, mod, cap, auto, crr, year);
            avionService.add(newAvion);

            System.out.println("Avion ajouté avec succès !");

        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un nombre valide pour la capacité, les crashs ou l'année.");
        }
    }


    public static void suppPlane() {
        System.out.println("\n=== SUPPRESSION D'UN AVION ===");
        System.out.println("Tapez 'menu' pour revenir au menu principal.\n");
        System.out.print("ID de l'avion à supprimer : ");
        String idStr = sc.nextLine().trim();
        if (idStr.equalsIgnoreCase("menu")) return;
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un nombre valide.");
            return;
        }
        // Vérification que l'avion existe (optionnel mais conseillé)
        Avion avion = avionService.getAll().stream()
                         .filter(a -> a.getId() == id)
                         .findFirst()
                         .orElse(null);
        
        if (avion == null) {
            System.out.println("ID introuvable.");
            return;
        }
        System.out.println("Vous êtes sur le point de supprimer l'avion : " + avion);
        System.out.print("Confirmez-vous la suppression ? (O/N) : ");
        String confirmation = sc.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("O")) {
            System.out.println("Suppression annulée.");
            return;
        }
        boolean ok = avionService.delete(id);
        System.out.println(ok ? "Suppression réussie !" : "Échec de la suppression.");
    }

    public static void suppCrash() {
        System.out.println("\n=== SUPPRESSION D'UN CRASH ===");
        System.out.println("Tapez 'menu' pour revenir au menu principal.\n");
        System.out.print("ID du crash à supprimer : ");
        String crashIdStr = sc.nextLine().trim();
        if (crashIdStr.equalsIgnoreCase("menu")) return;      
        int crashId;
        try {
            crashId = Integer.parseInt(crashIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un nombre valide.");
            return;
        }
        // Vérification que le crash existe (optionnel mais conseillé)
        Crash crash = crashService.getAll().stream()
                         .filter(c -> c.getId() == crashId)
                         .findFirst()
                         .orElse(null);
        
        if (crash == null) {
            System.out.println("ID introuvable.");
            return;
        }
        // Demander confirmation
        System.out.println("Vous êtes sur le point de supprimer le crash : " + crash);
        System.out.print("Confirmez-vous la suppression ? (O/N) : ");
        String confirmation = sc.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("O")) {
            System.out.println("Suppression annulée.");
            return;
        }

        boolean supprOk = crashService.deleteCrash(crashId);
        System.out.println(supprOk ? "Crash supprimé avec succès !" : "Échec de la suppression.");
    }

    
    public static void afficherAvion() {
    	List<Avion> all = avionService.getAll();
        if (!all.isEmpty()) all.forEach(System.out::println);
        else System.out.println("Aucun avion trouvé.");
    }
    
    public static void RechercheModele() {
    	String modele = AvionService.choisirModele(avionService, sc);
        if (modele != null) {
            Avion avion = avionService.searchByModel(modele);
            if (avion != null) System.out.println(avion);
            else System.out.println("Aucun avion trouvé pour ce modèle.");
        }
    }
    
    public static void RechercheConstructeur() {
    	String fabricant = AvionService.choisirConstructeur(avionService, sc);
        if (fabricant != null) {
            List<Avion> fabList = avionService.searchByFabricant(fabricant);
            if (!fabList.isEmpty()) fabList.forEach(System.out::println);
            else System.out.println("Aucun avion trouvé pour ce constructeur.");
        }
    }
    
    public static void modifyPlane() {
        System.out.println("\n=== MODIFICATION D'UN AVION ===");
        System.out.println("Tapez 'menu' à n'importe quelle étape pour revenir au menu principal.\n");

        List<Avion> avions = avionService.getAll();
        if (avions.isEmpty()) {
            System.out.println("Aucun avion disponible pour modification.");
            return;
        }

        // Affichage de tous les avions
        System.out.println("Liste des avions :");
        for (Avion a : avions) {
            System.out.println(a.getId() + ". " + a.getFabricant() + " " + a.getModele() +
                    " | Cap: " + a.getCapacite() + " | Auto: " + a.getAutonomie() +
                    " | Crashs: " + a.getCrashs() + " | Année: " + a.getAnneeService());
        }
        System.out.println("0. Retour au menu principal (ou tapez 'menu')");

        // Choix de l'avion à modifier
        Avion selectedAvion = null;
        while (true) {
            System.out.print("Entrez l'ID de l'avion à modifier : ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("menu") || input.equals("0")) return;

            try {
                int id = Integer.parseInt(input);
                for (Avion a : avions) {
                    if (a.getId() == id) {
                        selectedAvion = a;
                        break;
                    }
                }
                if (selectedAvion != null) break;
                else System.out.println("ID incorrect. Réessayez.");
            } catch (NumberFormatException e) {
                System.out.println("ID incorrect. Réessayez.");
            }
        }

        try {
            // --- MODIFICATION DU CONSTRUCTEUR ---
            System.out.println("Constructeur actuel : " + selectedAvion.getFabricant());

            List<String> allConstructeurs = avionService.getAllConstructeurs();
            String fab = selectedAvion.getFabricant();

            if (!allConstructeurs.isEmpty()) {
                System.out.println("Constructeurs existants :");
                for (int i = 0; i < allConstructeurs.size(); i++) {
                    System.out.println((i + 1) + ". " + allConstructeurs.get(i));
                }
                System.out.print("Sélectionnez un constructeur par numéro ou ENTER pour garder : ");
                String choixFab = sc.nextLine().trim();
                if (choixFab.equalsIgnoreCase("menu")) return;
                if (!choixFab.isEmpty()) {
                    try {
                        int numFab = Integer.parseInt(choixFab);
                        if (numFab < 1 || numFab > allConstructeurs.size()) {
                            System.out.println("Numéro incorrect.");
                            return;
                        }
                        fab = allConstructeurs.get(numFab - 1);
                        selectedAvion.setFabricant(fab);
                    } catch (NumberFormatException e) {
                        System.out.println("Numéro incorrect.");
                        return;
                    }
                }
            } else {
                System.out.print("Nouveau constructeur : ");
                String newFab = sc.nextLine().trim();
                if (newFab.equalsIgnoreCase("menu")) return;
                if (!newFab.isEmpty()) fab = newFab;
                selectedAvion.setFabricant(fab);
            }

            boolean constructeurExiste = avionService.getAllConstructeurs().contains(fab);

            // --- MODIFICATION DU MODÈLE ---
            if (constructeurExiste) {
                List<String> modeles = avionService.getModelesByFabricant(fab);
                if (!modeles.isEmpty()) {
                    System.out.println("Modèles existants pour ce constructeur :");
                    for (int i = 0; i < modeles.size(); i++) {
                        System.out.println((i + 1) + ". " + modeles.get(i));
                    }
                    System.out.print("Sélectionnez un modèle par numéro ou ENTER pour garder : ");
                    String choixMod = sc.nextLine().trim();
                    if (choixMod.equalsIgnoreCase("menu")) return;
                    if (!choixMod.isEmpty()) {
                        try {
                            int numMod = Integer.parseInt(choixMod);
                            if (numMod < 1 || numMod > modeles.size()) {
                                System.out.println("Numéro incorrect.");
                                return;
                            }
                            selectedAvion.setModele(modeles.get(numMod - 1));
                        } catch (NumberFormatException e) {
                            System.out.println("Numéro incorrect.");
                            return;
                        }
                    }
                } else {
                    System.out.print("Nouveau modèle : ");
                    String newMod = sc.nextLine().trim();
                    if (newMod.equalsIgnoreCase("menu")) return;
                    selectedAvion.setModele(newMod);
                }
            } else {
                System.out.print("Ce constructeur est nouveau. Saisissez un modèle : ");
                String newMod = sc.nextLine().trim();
                if (newMod.equalsIgnoreCase("menu")) return;
                selectedAvion.setModele(newMod);
            }

            // --- MODIFICATION DES AUTRES CHAMPS ---
            System.out.println("Capacité actuelle : " + selectedAvion.getCapacite());
            System.out.print("Nouvelle capacité (ou ENTER pour garder) : ");
            String capStr = sc.nextLine().trim();
            if (capStr.equalsIgnoreCase("menu")) return;
            if (!capStr.isEmpty()) selectedAvion.setCapacite(Integer.parseInt(capStr));

            System.out.println("Autonomie actuelle : " + selectedAvion.getAutonomie());
            System.out.print("Nouvelle autonomie (km) (ou ENTER pour garder) : ");
            String autoStr = sc.nextLine().trim();
            if (autoStr.equalsIgnoreCase("menu")) return;
            if (!autoStr.isEmpty()) selectedAvion.setAutonomie(Integer.parseInt(autoStr));

            System.out.println("Nombre de crashs actuel : " + selectedAvion.getCrashs());
            System.out.print("Nouveau nombre de crashs (ou ENTER pour garder) : ");
            String crStr = sc.nextLine().trim();
            if (crStr.equalsIgnoreCase("menu")) return;
            if (!crStr.isEmpty()) selectedAvion.setCrashs(Integer.parseInt(crStr));

            System.out.println("Année actuelle : " + selectedAvion.getAnneeService());
            System.out.print("Nouvelle année (ou ENTER pour garder) : ");
            String yearStr = sc.nextLine().trim();
            if (yearStr.equalsIgnoreCase("menu")) return;
            if (!yearStr.isEmpty()) {
                int year = Integer.parseInt(yearStr);
                if (year < 1903 || year > 2025) {
                    System.out.println("Année invalide ! Elle doit être comprise entre 1903 et 2025.");
                    return;
                }
                selectedAvion.setAnneeService(year);
            }

            // --- MISE À JOUR EN BDD ---
            String sql = "UPDATE avions SET fabricant = ?, modele = ?, capacite = ?, autonomie = ?, crashs = ?, annee_service = ? WHERE id = ?";
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, selectedAvion.getFabricant());
                ps.setString(2, selectedAvion.getModele());
                ps.setInt(3, selectedAvion.getCapacite());
                ps.setInt(4, selectedAvion.getAutonomie());
                ps.setInt(5, selectedAvion.getCrashs());
                ps.setInt(6, selectedAvion.getAnneeService());
                ps.setInt(7, selectedAvion.getId());

                ps.executeUpdate();
            }

            System.out.println("Avion modifié avec succès !");
        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un nombre valide.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

}


