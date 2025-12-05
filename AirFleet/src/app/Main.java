package app;

import java.util.List;
import java.util.Scanner;
import service.Function;
import model.Avion;
import model.Crash;
import service.AvionService;
import service.CrashService;

public class Main {

    public static void main(String[] args) {

        AvionService avionService = new AvionService();
        CrashService crashService = new CrashService();
        Scanner sc = new Scanner(System.in);
        Function.afficherBienvenue();
        int choix;
        do {
            System.out.println("\n===== BASE DE DONNÉES AVIONS =====");
            System.out.println("1. Afficher tous les avions");
            System.out.println("2. Rechercher un avion par modèle");
            System.out.println("3. Rechercher par constructeur");
            System.out.println("4. Ajouter un avion");
            System.out.println("5. Modifier un avion");
            System.out.println("6. Supprimer un avion");
            System.out.println("7. Statistiques & Crashs");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            String line = sc.nextLine().trim();
            if (line.isEmpty()) choix = -1;
            else {
                try { choix = Integer.parseInt(line); }
                catch (NumberFormatException e) { choix = -1; }
            }

            switch (choix) {
                case 1: // Afficher tous les avions
                    Function.afficherAvion();
                    break;

                case 2: // Recherche par modèle
                    Function.RechercheModele();
                    break;

                case 3: // Recherche par constructeur
                    Function.RechercheConstructeur();
                    break;

                case 4: // Ajouter un avion
                    Function.addPlane();
                    break;

                case 5: // Modifier un avion
                    Function.modifyPlane();
                    break;

                case 6: // Supprimer un avion
                    Function.suppPlane();
                    break;

                case 7: // Statistiques
                    int sousChoix;
                    do {
                        System.out.println("\n===== STATISTIQUES & CRASHS =====");
                        System.out.println("1. Résumé constructeur");
                        System.out.println("2. Rechercher crashs par modèle");
                        System.out.println("3. Rechercher crashs par constructeur");
                        System.out.println("4. Afficher tous les crashs");
                        System.out.println("5. Ajouter un crash");
                        System.out.println("6. Supprimer un crash");
                        System.out.println("0. Retour au menu principal");
                        System.out.print("Votre choix : ");

                        String subLine = sc.nextLine().trim();
                        if (subLine.isEmpty()) sousChoix = -1;
                        else {
                            try { sousChoix = Integer.parseInt(subLine); }
                            catch (NumberFormatException e) { sousChoix = -1; }
                        }

                        if (sousChoix == 0) {
                            System.out.println("Retour au menu principal...");
                            break;
                        }

                        switch (sousChoix) {
                            case 1: // Résumé constructeur
                                Function.tri();
                                break;

                            case 2: // Rechercher crashs par modèle
                                String modelCrash = AvionService.choisirModele(avionService, sc);
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
                                String fabCrash = AvionService.choisirConstructeur(avionService, sc);
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
                                Function.addCrash();
                                break;

                            case 6: // Supprimer un crash
                                Function.suppCrash();
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
