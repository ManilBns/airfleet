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
    
    public static void afficherBienvenue() {
        System.out.println("\n");
        System.out.println("                __|__");
        System.out.println("         -------(   )-------");
        System.out.println("   ************* / V \\ *************");
        System.out.println("                  ***");
        System.out.println("\n         Bienvenue sur AirApp !");
        System.out.println("      Votre répertoire d'avions\n");
    }
    
    public static void resume() {	
	    String fabStat = AvionService.choisirConstructeur(avionService, sc);
	    if (fabStat != null) {
	        List<Avion> avionsFilter = avionService.searchByFabricant(fabStat); // la liste des avions du constructeur fabStat
	        if (!avionsFilter.isEmpty()) {
	            Avion[] tabFilter = avionsFilter.toArray(new Avion[0]); //on convertit en tableau car dans jni on attend un tableau pour les tris
	            System.out.println("Moyenne autonomie : " + nativeLib.moyenneAutonomie(tabFilter));
	            System.out.println("Moyenne de crash : " + nativeLib.moyenneCrashs(tabFilter));
	            int totalCrashs = crashService.countByConstructeur(fabStat);
	            int totalMorts = crashService.totalMortsParFabricant(fabStat);
	            System.out.println("Nombre total de crashs : " + totalCrashs);
	            System.out.println("Nombre total de morts : " + totalMorts);
	            Avion[] topFiabilite = nativeLib.trierParCrashs(tabFilter);
	            System.out.println("\nTop 3 avions les plus fiables :");
	            for (int i = 0; i < Math.min(3, topFiabilite.length); i++) // Math.min(3, topFiabilite.length) prend le min entre 3 et nombre d'elt dans tableau
	                System.out.println((i + 1) + ". " + topFiabilite[i] +
	                        " (Crashs : " + crashService.countByAvion(topFiabilite[i].getId()) + ")");	
	            Avion[] topAutonomie = nativeLib.trierParAutonomie(tabFilter);
	            System.out.println("\nTop 3 avions les plus autonomes :");
	            for (int i = topAutonomie.length - 1; i >= Math.max(0, topAutonomie.length - 3); i--) /*on parcours le tableau a l'envers pour prendre les plus autonomes , 
	            topAutonomie.length - 3 = 3eme indice depuis  la fin , mathmax(0..) s'assure que le tableau ne descende jamais a un indice negatif pour eviter une erreur				*/
	                System.out.println((topAutonomie.length - i) + ". " + topAutonomie[i]);	
	            Avion pireAvion = topFiabilite[topFiabilite.length - 1]; //topFiabilite.length - 1 = dernier indice donc l'avion le moins fiable de la liste des avions 
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
            List<Avion> avions = avionService.getAll();
            if (avions.isEmpty()) {
                System.out.println("Aucun avion disponible. Ajout impossible.");
                return;
            }
            // Affichage de tous les avions
            System.out.println("Liste des avions :");
            for (Avion a : avions) {
                System.out.println(a.getId() + ". " + a.getFabricant() + " " + a.getModele() +
                                   " | Capacité: " + a.getCapacite() + " | Autonomie: " + a.getAutonomie() +
                                   " | Crashs: " + a.getCrashs() + " | Année: " + a.getAnneeService());
            }
            // Choix de l'avion
            Avion avion = null;
            while (avion == null) {
                System.out.print("ID de l'avion : ");
                String avionIdStr = sc.nextLine().trim();
                if (avionIdStr.equalsIgnoreCase("menu")) return;
                if (avionIdStr.isEmpty()) {
                    System.out.println("Insérer un numéro !");
                    continue; //revient au debut de la boucle
                }
                try {
                    final int idTemp = Integer.parseInt(avionIdStr); //final car idTemp est utilisé dans une expression lambda 
                    avion = avions.stream().filter(a -> a.getId() == idTemp).findFirst().orElse(null); //stream sert a transformer une collection en flux d'élements avec lequel on peut intéragir
                    if (avion == null) {
                        System.out.println("Vous essayez d'ajouter un crash sur un avion qui n'existe pas !");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez insérer un numéro valide !");
                }
            }
            // On récupère automatiquement le modèle de l'avion a partir de l'id
            String crashModele = avion.getModele();
            System.out.println("Modèle de l'avion sélectionné : " + crashModele);

            // Date du crash
            Date dateCrash = null;
            while (dateCrash == null) {
                System.out.print("Date du crash (YYYY-MM-DD) : ");
                String dateStr = sc.nextLine().trim();
                if (dateStr.equalsIgnoreCase("menu")) return;
                try {
                    Date tempDate = Date.valueOf(dateStr);
                    int anneeCrash = tempDate.toLocalDate().getYear(); //avoir l'annee du crash seulement
                    if (anneeCrash < avion.getAnneeService()) {
                        System.out.println("L'année du crash doit être >= année de mise en service (" + avion.getAnneeService() + ").");
                    } else {
                        dateCrash = tempDate;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Format de date invalide ! Utilisez YYYY-MM-DD.");
                }
            }
            // Lieu
            System.out.print("Lieu : ");
            String lieu = sc.nextLine().trim();
            if (lieu.equalsIgnoreCase("menu")) return;
            // Gravité (choix dynamique)
            String[] gravites = {"mineur", "sérieux", "catastrophique"};
            System.out.println("Gravité :");
            for (int i = 0; i < gravites.length; i++) {
                System.out.println((i + 1) + ". " + gravites[i]);
            }
            String gravite = "";
            while (true) {
                System.out.print("Sélectionnez un numéro pour la gravité : ");
                String choixGravite = sc.nextLine().trim();
                if (choixGravite.equalsIgnoreCase("menu")) return;
                try {
                    int num = Integer.parseInt(choixGravite);
                    if (num >= 1 && num <= gravites.length) {
                        gravite = gravites[num - 1];
                        break;
                    } else {
                        System.out.println("Veuillez choisir un numéro valide parmi 1, 2 ou 3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez entrer un nombre valide pour la gravité !");
                }
            }
            // Nombre de morts
            int morts = -1;
            while (morts < 0) {
                System.out.print("Nombre de morts : ");
                String mortsStr = sc.nextLine().trim();
                if (mortsStr.equalsIgnoreCase("menu")) return;
                try {
                    morts = Integer.parseInt(mortsStr);
                } catch (NumberFormatException e) {
                    System.out.println("Insérer un numéro !");
                }
            }

            // Nombre de blessés
            int blesses = -1;
            while (blesses < 0) {
                System.out.print("Nombre de blessés : ");
                String blessesStr = sc.nextLine().trim();
                if (blessesStr.equalsIgnoreCase("menu")) return;
                try {
                    blesses = Integer.parseInt(blessesStr);
                } catch (NumberFormatException e) {
                    System.out.println("Insérer un numéro !");
                }
            }
            // Cause
            System.out.print("Cause : ");
            String cause = sc.nextLine().trim();
            if (cause.equalsIgnoreCase("menu")) return;

            // Description
            System.out.print("Description : ");
            String desc = sc.nextLine().trim();
            if (desc.equalsIgnoreCase("menu")) return;

            // Création du crash
            Crash newCrash = new Crash(0, avion.getId(), crashModele, dateCrash, lieu, gravite, morts, blesses, cause, desc);
            crashService.addCrash(newCrash);
            System.out.println("Crash ajouté avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public static void addPlane() {
        System.out.println("\n=== AJOUT D'UN AVION ===");
        System.out.println("Tapez 'menu' à n'importe quelle étape pour revenir au menu principal.\n");
        try {
            // --- CHOIX DU CONSTRUCTEUR ---
            String fab;
            boolean constructeurExiste = false;
            while (true) {
                System.out.print("Voulez-vous choisir un constructeur existant ? (O/N) : ");
                String choixFab = sc.nextLine().trim();
                if (choixFab.equalsIgnoreCase("menu")) return;
                if (!choixFab.equalsIgnoreCase("O") && !choixFab.equalsIgnoreCase("N")) {
                    System.out.println("Choisir seulement 'O' ou 'N'");
                    continue;
                }

                if (choixFab.equalsIgnoreCase("O")) {
                    fab = AvionService.choisirConstructeur(avionService, sc);
                    if (fab == null) return; // retour menu car il n y a aucun fabricant
                    constructeurExiste = true;
                } else {
                    System.out.print("Saisissez le constructeur : ");
                    fab = sc.nextLine().trim();
                    if (fab.equalsIgnoreCase("menu")) return;
                    // Vérifier si le constructeur saisis existe ou pas
                    List<String> existing = avionService.getAllConstructeurs();
                    constructeurExiste = existing.contains(fab);
                }
                break;
            }
            //CHOIX DU MODÈLE
            String mod;
            if (constructeurExiste) { //si le constructeur existait déjà alors :
                while (true) {
                    System.out.print("Voulez-vous choisir un modèle existant ? (O/N) : ");
                    String choixModele = sc.nextLine().trim();
                    if (choixModele.equalsIgnoreCase("menu")) return;
                    if (!choixModele.equalsIgnoreCase("O") && !choixModele.equalsIgnoreCase("N")) {
                        System.out.println("Choisir seulement 'O' ou 'N'");
                        continue;
                    }

                    if (choixModele.equalsIgnoreCase("O")) {
                        mod = AvionService.choisirModelePourConstructeur(avionService, sc, fab); //afficher seulement les models du fabricant fab
                        if (mod == null) return; // retour menu si aucun model n'existe
                    } else {
                        System.out.print("Saisissez le modèle : ");
                        mod = sc.nextLine().trim();
                        if (mod.equalsIgnoreCase("menu")) return;
                    }
                    break;
                }
            } else {
                // Nouveau constructeur → modèle obligatoire
                System.out.println("Ce constructeur n'existe pas encore vous devez saisir un nouveau modèle.");
                System.out.print("Modèle : ");
                mod = sc.nextLine().trim();
                if (mod.equalsIgnoreCase("menu")) return;
            }

            //SAISIE DES AUTRES INFORMATIONS
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
            // AJOUT EN BDD
            Avion newAvion = new Avion(0, fab, mod, cap, auto, crr, year); //0 car auto incrément dans la bdd
            avionService.add(newAvion);
            System.out.println("Avion ajouté avec succès !");
        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un entier valide pour la capacité, les crashs ou l'année.");
        }
    }

    public static void suppPlane() {
        System.out.println("\n=== SUPPRESSION D'UN AVION ===");
        System.out.println("Tapez 'menu' pour revenir au menu principal.\n");

        // Afficher tous les avions disponibles
        List<Avion> avions = avionService.getAll();
        if (avions.isEmpty()) {
            System.out.println("Aucun avion disponible pour suppression.");
            return;
        }
        System.out.println("Liste des avions :");
        for (Avion a : avions) {
            System.out.println(a.getId() + ". " + a.getFabricant() + " " + a.getModele() +
                               " | Capacité: " + a.getCapacite() + " | Autonomie: " + a.getAutonomie() +
                               " | Crashs: " + a.getCrashs() + " | Année: " + a.getAnneeService());
        }

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
        //vérifier si l'id a supp existe
        Avion avion = avions.stream().filter(a -> a.getId() == id) .findFirst() .orElse(null);
        if (avion == null) {
            System.out.println("ID introuvable.");
            return;
        }
        System.out.println("Vous êtes sur le point de supprimer l'avion : " + avion);
        //CONFIRMATION O/N
        while (true) {
            System.out.print("Confirmez-vous la suppression ? (O/N) : ");
            String confirmation = sc.nextLine().trim();
            if (confirmation.equalsIgnoreCase("menu")) return;
            if (!confirmation.equalsIgnoreCase("O") && !confirmation.equalsIgnoreCase("N")) {
                System.out.println("Choisir seulement 'O' ou 'N'");
                continue;
            }
            if (confirmation.equalsIgnoreCase("O")) {
                boolean ok = avionService.delete(id);
                System.out.println(ok ? "Suppression réussie !" : "Échec de la suppression.");
            } else {
                System.out.println("Suppression annulée.");
            }
            break;
        }
    }

    public static void suppCrash() {
        System.out.println("\n=== SUPPRESSION D'UN CRASH ===");
        System.out.println("Tapez 'menu' pour revenir au menu principal.\n");
        // Afficher tous les crashs disponibles
        List<Crash> crashes = crashService.getAll();
        if (crashes.isEmpty()) {
            System.out.println("Aucun crash disponible pour suppression.");
            return;
        }
        System.out.println("Liste des crashs :");
        for (Crash c : crashes) {
            System.out.println(c.getId() + ". Avion ID: " + c.getAvionId() + " | Modèle: " + c.getModele() +
                               " | Date: " + c.getDateCrash() + " | Lieu: " + c.getLieu() + 
                               " | Gravité: " + c.getGravite());
        }

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
        Crash crash = crashes.stream() .filter(c -> c.getId() == crashId).findFirst() .orElse(null);
        //verifier si l'id à sup existe
        if (crash == null) {
            System.out.println("ID introuvable.");
            return;
        }
        System.out.println("Vous êtes sur le point de supprimer le crash : " + crash);
        //confirmation O/N
        while (true) {
            System.out.print("Confirmez-vous la suppression ? (O/N) : ");
            String confirmation = sc.nextLine().trim();
            if (confirmation.equalsIgnoreCase("menu")) return;
            if (!confirmation.equalsIgnoreCase("O") && !confirmation.equalsIgnoreCase("N")) {
                System.out.println("Choisir seulement 'O' ou 'N'");
                continue;
            }
            if (confirmation.equalsIgnoreCase("O")) {
                boolean supprOk = crashService.deleteCrash(crashId);
                System.out.println(supprOk ? "Crash supprimé avec succès !" : "Échec de la suppression.");
            } else {
                System.out.println("Suppression annulée.");
            }
            break;
        }
    }

    public static void afficherAvion() {
    	List<Avion> all = avionService.getAll();
        if (!all.isEmpty()) all.forEach(System.out::println);
        else System.out.println("Aucun avion trouvé.");
    }
    
    public static void RechercheModele() {
    	String modele = AvionService.choisirModele(avionService, sc); //on selectionne le modele a partir du choix dynamique grace a choisirModele
        if (modele != null) {
            Avion avion = avionService.searchByModel(modele); //afficher la liste des avions dont le modele est 'modele'
            if (avion != null) System.out.println(avion);
            else System.out.println("Aucun avion trouvé pour ce modèle.");
        }
    }
    
    public static void RechercheConstructeur() {
    	String fabricant = AvionService.choisirConstructeur(avionService, sc); //on selectionne fabricant a partir du choix dynamique grace a choisirConstructeur
        if (fabricant != null) {
            List<Avion> fabList = avionService.searchByFabricant(fabricant); //liste des avions dont le constructeur est 'fabricant'
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
                for (Avion a : avions) { //si l'id existe dans la liste d'avions alors selectedAvion devient l'avion de cet id et on sort de la boucle
                    if (a.getId() == id) {
                        selectedAvion = a;
                        break;
                    }
                }
                if (selectedAvion != null) break; //si selectedAvion est trouvé on sort du try 
                else System.out.println("ID incorrect. Réessayez.");
            } catch (NumberFormatException e) {
                System.out.println("ID incorrect. Réessayez.");
            }
        }
        try {
            //MODIFICATION DU CONSTRUCTEUR
            System.out.println("Constructeur actuel : " + selectedAvion.getFabricant());
            List<String> allConstructeurs = avionService.getAllConstructeurs();
            String fab = selectedAvion.getFabricant(); //le fabricant de l'avion choisis précédemment
            if (!allConstructeurs.isEmpty()) { //afficher choix dynamique de tous les constructeurs
                System.out.println("Constructeurs existants :");
                for (int i = 0; i < allConstructeurs.size(); i++) {
                    System.out.println((i + 1) + ". " + allConstructeurs.get(i));
                }
            }
            System.out.print("Sélectionnez un constructeur par numéro / tapez ENTER pour garder / saisir un nouveau : ");
            String choixFab = sc.nextLine().trim();
            if (choixFab.equalsIgnoreCase("menu")) return;
            if (!choixFab.isEmpty()) {  //s'il n'a pas appuyer sur entré directement
                boolean choixValide = false;
                // Vérifier si c'est un numéro existant
                try { 
                	//tester si le choix de l'utilisateur fais partie des choix dynamique proposés
                    int numFab = Integer.parseInt(choixFab);
                    if (numFab >= 1 && numFab <= allConstructeurs.size()) {
                        fab = allConstructeurs.get(numFab - 1); //récuperer le bon constructeur de la liste de choix dynamique, get(numFab - 1) = s'il tape 1 il aura le premier constructeur allConstructeur.get(0) 
                        selectedAvion.setFabricant(fab); //l'avion prendra ce fabricant
                        choixValide = true;
                    }
                } catch (NumberFormatException ignored) {} //pour permettre la saisie manuelle

                // Saisie d'un nouveau constructeur libre si le choix n'était pas valide plus haut (on a pas choisis un des numéros du choix dynamique)
                if (!choixValide) {            
                    fab = choixFab; //fab prend la valeur saisie manuellement
                    selectedAvion.setFabricant(fab);
                    
                    //saisis manuelle du modèle pour le constructeur (car le constructeur était inexsitant auparavant) 
                    System.out.print("Nouveau modèle pour ce constructeur : ");
                    String newMod = sc.nextLine().trim();
                    if (newMod.equalsIgnoreCase("menu")) {
                        return;
                    }
                    if(newMod.isEmpty()) {
                    	System.out.println("Ne pas entrer de modèle vide ! ");
                    	return;
                    }
                    selectedAvion.setModele(newMod);                     
                }
            }
            //MODIFICATION DU MODÈLE
            boolean constructeurExiste = false;
            //pour tous les constructeur vérifier si fab fais partie des constructeurs ( fabricants )
            for (String c : allConstructeurs) {
                if (c.equalsIgnoreCase(fab)) { // normaliser la casse pour BDD
                    fab = c; 
                    constructeurExiste = true;
                    selectedAvion.setFabricant(fab); //l'avion recoit fab comme nouveau constructeur
                    break;
                }
            }
            //fab fais partie des fabricants
            if (constructeurExiste) {
                List<String> modeles = avionService.getModelesByFabricant(fab); //lister tous les modèles du fabricant fab
                if (!modeles.isEmpty()) {
                    System.out.println("Modèles existants pour ce constructeur :");
                    //afficher la liste de tous les modeles de fab
                    for (int i = 0; i < modeles.size(); i++) {
                        System.out.println((i + 1) + ". " + modeles.get(i));
                    }
                    System.out.print("Sélectionnez un modèle par numéro / Tapez ENTER pour garder / Saisir un nouveau : ");
                    String choixMod = sc.nextLine().trim();
                    if (choixMod.equalsIgnoreCase("menu")) return;
                    if (!choixMod.isEmpty()) {
                        try {
                            int numMod = Integer.parseInt(choixMod);
                            //si le choix fais partie des numéros du choix dynamique
                            if (numMod >= 1 && numMod <= modeles.size()) {
                                selectedAvion.setModele(modeles.get(numMod - 1));
                            } else {
                                selectedAvion.setModele(choixMod); // saisie libre
                            }
                        } catch (NumberFormatException e) {
                            selectedAvion.setModele(choixMod); // saisie libre
                        }
                    }
                } /*else {
                    System.out.print("Aucun modèle existant. Saisissez un nouveau modèle : ");
                    String newMod = sc.nextLine().trim();
                    if (newMod.equalsIgnoreCase("menu") || newMod.isEmpty()) {
                        System.out.println("Vous devez saisir un modèle.");
                        return;
                    }
                    selectedAvion.setModele(newMod);
                } */
            }
            // --- MODIFICATION DES AUTRES CHAMPS ---
            System.out.println("Capacité actuelle : " + selectedAvion.getCapacite()); //montre la capa actuelle
            System.out.print("Nouvelle capacité (ou ENTER pour garder) : ");
            String capStr = sc.nextLine().trim(); //l'utilisateur saisis la nouvelle capa
            if (capStr.equalsIgnoreCase("menu")) return;
            if (!capStr.isEmpty()) selectedAvion.setCapacite(Integer.parseInt(capStr)); //si capStr contient quelque chose alors sera set comme nouvelle capa

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
            // MISE À JOUR EN BDD
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
    
    public static void rechercheCrashParModele() {
    	String modelCrash = AvionService.choisirModele(avionService, sc); //choisir le modele d'avion
        if (modelCrash != null) {
            List<Crash> crashess = crashService.getByModele(modelCrash); //liste de tous les crash du modeles "modelCrash"
            if (!crashess.isEmpty()) {
                for (Crash c : crashess) {
                    System.out.println("\n" + c + "\n");
                }
            } else System.out.println("Aucun crash trouvé pour ce modèle.");
        }
    }
    
    public static void rechercheCrashParConstructeur() {
    	  String fabCrash = AvionService.choisirConstructeur(avionService, sc); //choisir le fabricant
          if (fabCrash != null) {
              List<Crash> crashes = crashService.getByFabricant(fabCrash); //liste de tous les crashs du fabricant'fabCrash'
              if (!crashes.isEmpty()) {
                  for (Crash c : crashes) {
                      System.out.println("\n" + c + "\n");
                  }
              } else System.out.println("Aucun crash trouvé pour ce constructeur.");
          }
    }
    
    public static void afficherTousLesCrashs(){
    	List<Crash> allCrashs = crashService.getAll();
        if (!allCrashs.isEmpty()) {
            for (Crash c : allCrashs) {
                System.out.println("\n" + c + "\n");
            }
        } else System.out.println("Aucun crash enregistré.");
    }
}