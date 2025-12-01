package app;

import java.util.List;
import java.util.Scanner;
import java.sql.Date;

import model.Avion;
import model.Crash;
import service.AvionService;
import service.CrashService;
import nativeLib.NativeLib;

public class Main {

    // Méthode pour choisir un constructeur parmi ceux existants
    public static String choisirConstructeur(AvionService avionService, Scanner sc) {
        List<String> constructeurs = avionService.getAllConstructeurs();
        if (constructeurs.isEmpty()) {
            System.out.println("Aucun constructeur disponible.");
            return null;
        }

        System.out.println("Sélectionnez un constructeur :");
        for (int i = 0; i < constructeurs.size(); i++) {
            System.out.println((i + 1) + ". " + constructeurs.get(i));
        }

        int choix = -1;
        do {
            System.out.print("Votre choix (numéro) : ");
            String line = sc.nextLine().trim();
            try { choix = Integer.parseInt(line); }
            catch (NumberFormatException e) { choix = -1; }
        } while (choix < 1 || choix > constructeurs.size());

        return constructeurs.get(choix - 1);
    }

    // Méthode pour choisir un modèle parmi ceux existants
    public static String choisirModele(AvionService avionService, Scanner sc) {
        List<String> modeles = avionService.getAllModele();
        if (modeles.isEmpty()) {
            System.out.println("Aucun modèle disponible.");
            return null;
        }

        System.out.println("Sélectionnez un modèle :");
        for (int i = 0; i < modeles.size(); i++) {
            System.out.println((i + 1) + ". " + modeles.get(i));
        }

        int choix = -1;
        do {
            System.out.print("Votre choix (numéro) : ");
            String line = sc.nextLine().trim();
            try { choix = Integer.parseInt(line); }
            catch (NumberFormatException e) { choix = -1; }
        } while (choix < 1 || choix > modeles.size());

        return modeles.get(choix - 1);
    }

    public static void main(String[] args) {

        AvionService avionService = new AvionService();
        CrashService crashService = new CrashService();
        NativeLib nativeLib = new NativeLib();
        Scanner sc = new Scanner(System.in);

        int choix;

        do {
            System.out.println("\n===== BASE DE DONNÉES AVIONS =====");
            System.out.println("1 - Afficher tous les avions");
            System.out.println("2 - Rechercher un avion par modèle");
            System.out.println("3 - Rechercher par constructeur");
            System.out.println("4 - Ajouter un avion");
            System.out.println("5 - Supprimer un avion");
            System.out.println("6 - Statistiques");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");

            String line = sc.nextLine().trim();
            if (line.isEmpty()) choix = -1;
            else {
                try { choix = Integer.parseInt(line); }
                catch (NumberFormatException e) { choix = -1; }
            }

            switch (choix) {
                case 1: // Afficher tous les avions
                    List<Avion> all = avionService.getAll();
                    if (!all.isEmpty()) all.forEach(System.out::println);
                    else System.out.println("Aucun avion trouvé.");
                    break;

                case 2: // Recherche par modèle
                    String modele = choisirModele(avionService, sc);
                    if (modele != null) {
                        Avion avion = avionService.searchByModel(modele); // un seul avion
                        if (avion != null) System.out.println(avion);
                        else System.out.println("Aucun avion trouvé pour ce modèle.");
                    }
                    break;

                case 3: // Recherche par constructeur
                    String fabricant = choisirConstructeur(avionService, sc);
                    if (fabricant != null) {
                        List<Avion> fabList = avionService.searchByFabricant(fabricant);
                        if (!fabList.isEmpty()) fabList.forEach(System.out::println);
                        else System.out.println("Aucun avion trouvé pour ce constructeur.");
                    }
                    break;

                case 4: // Ajouter un avion
                    System.out.print("Fabricant : ");
                    String fab = sc.nextLine().trim();
                    System.out.print("Modèle : ");
                    String mod = sc.nextLine().trim();
                    System.out.print("Capacité : ");
                    int cap = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Autonomie (km) : ");
                    int auto = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Année d'entrée en service : ");
                    int year = Integer.parseInt(sc.nextLine().trim());

                    Avion newAvion = new Avion(0, fab, mod, cap, auto, 0, year);
                    avionService.add(newAvion);
                    System.out.println("Avion ajouté avec succès !");
                    break;

                case 5: // Supprimer un avion
                    System.out.print("ID de l’avion à supprimer : ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    boolean ok = avionService.delete(id);
                    System.out.println(ok ? "Suppression réussie !" : "Échec de la suppression (ID introuvable).");
                    break;

                case 6: // Statistiques
                    int sousChoix;
                    do {
                        System.out.println("\n===== STATISTIQUES =====");
                        System.out.println("1 - Résumé constructeur");
                        System.out.println("2 - Rechercher crashs par modèle");
                        System.out.println("3 - Rechercher crashs par constructeur");
                        System.out.println("4 - Afficher tous les crashs");
                        System.out.println("5 - Ajouter un crash");
                        System.out.println("6 - Supprimer un crash");
                        System.out.println("0 - Retour au menu principal");
                        System.out.print("Votre choix : ");

                        String subLine = sc.nextLine().trim();
                        if (subLine.isEmpty()) sousChoix = -1;
                        else {
                            try { sousChoix = Integer.parseInt(subLine); }
                            catch (NumberFormatException e) { sousChoix = -1; }
                        }

                        switch (sousChoix) {
                            case 1: // Résumé constructeur
                                String fabStat = choisirConstructeur(avionService, sc);
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
                                    } else System.out.println("Aucun avion trouvé pour ce constructeur.");
                                }
                                break;

                            case 2: // Rechercher crashs par modèle
                                String modelCrash = choisirModele(avionService, sc);
                                if (modelCrash != null) {
                                    List<Crash> crashess = crashService.getByModele(modelCrash);
                                    if (!crashess.isEmpty()) {
                                        for (Crash c : crashess) {
                                            System.out.println("\n" + c + "\n");
                                        }
                                    } else System.out.println("Aucun crash trouvé pour ce modèle.");
                                }
                                break;

                            case 3: // Rechercher crashs par constructeur
                                String fabCrash = choisirConstructeur(avionService, sc);
                                if (fabCrash != null) {
                                    List<Crash> crashes = crashService.getByFabricant(fabCrash);
                                    if (!crashes.isEmpty()) {
                                        for (Crash c : crashes) {
                                            System.out.println("\n" + c + "\n");
                                        }
                                    } else System.out.println("Aucun crash trouvé pour ce constructeur.");
                                }
                                break;

                            case 4: // Afficher tous les crashs
                                List<Crash> allCrashs = crashService.getAll();
                                if (!allCrashs.isEmpty()) {
                                    for (Crash c : allCrashs) {
                                        System.out.println("\n" + c + "\n");
                                    }
                                } else System.out.println("Aucun crash enregistré.");
                                break;

                            case 5: // Ajouter un crash
                                try {
                                    System.out.print("ID de l’avion : ");
                                    int avionId = Integer.parseInt(sc.nextLine().trim());
                                    String crashModele = choisirModele(avionService, sc);
                                    if (crashModele == null) break;

                                    System.out.print("Date du crash (YYYY-MM-DD) : ");
                                    String dateStr = sc.nextLine().trim();
                                    Date dateCrash = Date.valueOf(dateStr);
                                    System.out.print("Lieu : ");
                                    String lieu = sc.nextLine().trim();
                                    System.out.print("Gravité : ");
                                    String gravite = sc.nextLine().trim();
                                    System.out.print("Nombre de morts : ");
                                    int morts = Integer.parseInt(sc.nextLine().trim());
                                    System.out.print("Nombre de blessés : ");
                                    int blesses = Integer.parseInt(sc.nextLine().trim());
                                    System.out.print("Cause : ");
                                    String cause = sc.nextLine().trim();
                                    System.out.print("Description : ");
                                    String desc = sc.nextLine().trim();

                                    Crash newCrash = new Crash(0, avionId, crashModele, dateCrash, lieu, gravite, morts, blesses, cause, desc);
                                    crashService.addCrash(newCrash);
                                    System.out.println("Crash ajouté avec succès !");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Format de date invalide. Utiliser YYYY-MM-DD.");
                                }
                                break;

                            case 6: // Supprimer un crash
                                System.out.print("ID du crash à supprimer : ");
                                int crashId = Integer.parseInt(sc.nextLine().trim());
                                boolean supprOk = crashService.deleteCrash(crashId);
                                System.out.println(supprOk ? "Crash supprimé avec succès !" : "ID introuvable.");
                                break;

                            case 0:
                                System.out.println("Retour au menu principal...");
                                break;

                            default:
                                System.out.println("Choix invalide.");
                        }

                    } while (sousChoix != 0);
                    break;

                case 0:
                    System.out.println("Fermeture du programme...");
                    break;

                default:
                    System.out.println("Choix invalide.");
            }

        } while (choix != 0);

        sc.close();
    }
}
