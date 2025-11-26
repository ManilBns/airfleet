package app;

import java.util.List;
import java.util.Scanner;
import model.Avion;
import service.AvionService;
import nativeLib.NativeLib;

public class Main {

    public static void main(String[] args) {

        AvionService service = new AvionService();
        NativeLib nativeLib = new NativeLib();
        Scanner sc = new Scanner(System.in);

        int choix;

        do {
            System.out.println("\n===== BASE DE DONNÉES AVIONS =====");
            System.out.println("1 — Afficher tous les avions");
            System.out.println("2 — Rechercher un avion par modèle");
            System.out.println("3 — Rechercher par fabricant");
            System.out.println("4 — Ajouter un avion");
            System.out.println("5 — Supprimer un avion");
            System.out.println("6 — Statistiques");
            System.out.println("0 — Quitter");
            System.out.print("Votre choix : ");

            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                choix = -1;
            } else {
                try {
                    choix = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    choix = -1;
                }
            }

            switch (choix) {

                // ----------------------------------------------------
                // 1 — Afficher
                // ----------------------------------------------------
                case 1:
                    List<Avion> all = service.getAll();
                    if (!all.isEmpty()) all.forEach(System.out::println);
                    else System.out.println("Aucun avion trouvé.");
                    break;

                // ----------------------------------------------------
                // 2 — Recherche par modèle
                // ----------------------------------------------------
                case 2:
                    System.out.print("Modèle à rechercher : ");
                    String modele = sc.nextLine().trim();
                    Avion avion = service.searchByModel(modele);

                    if (avion != null) System.out.println("\n" + avion);
                    else System.out.println("Aucun avion trouvé avec ce modèle.");
                    break;

                // ----------------------------------------------------
                // 3 — Recherche par fabricant
                // ----------------------------------------------------
                case 3:
                    System.out.print("Fabricant (Airbus/Boeing) : ");
                    String fabricant = sc.nextLine().trim();
                    List<Avion> fabList = service.searchByFabricant(fabricant);

                    if (!fabList.isEmpty()) fabList.forEach(System.out::println);
                    else System.out.println("Aucun avion trouvé pour ce fabricant.");
                    break;

                // ----------------------------------------------------
                // 4 — Ajouter un avion
                // ----------------------------------------------------
                case 4:
                    System.out.print("Fabricant : ");
                    String fab = sc.nextLine().trim();

                    System.out.print("Modèle : ");
                    String mod = sc.nextLine().trim();

                    System.out.print("Capacité : ");
                    int cap = Integer.parseInt(sc.nextLine().trim());

                    System.out.print("Autonomie (km) : ");
                    int auto = Integer.parseInt(sc.nextLine().trim());

                    System.out.print("Nombre de crashs : ");
                    int crash = Integer.parseInt(sc.nextLine().trim());

                    System.out.print("Année d'entrée en service : ");
                    int year = Integer.parseInt(sc.nextLine().trim());

                    Avion newAvion = new Avion(0, fab, mod, cap, auto, crash, year);
                    service.add(newAvion);
                    System.out.println("Avion ajouté avec succès !");
                    break;

                // ----------------------------------------------------
                // 5 — Supprimer
                // ----------------------------------------------------
                case 5:
                    System.out.print("ID de l’avion à supprimer : ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    boolean ok = service.delete(id);

                    if (ok) System.out.println("Suppression réussie !");
                    else System.out.println("Échec de la suppression (ID introuvable).");
                    break;

                // ----------------------------------------------------
                // 6 — Statistiques (par constructeur)
                // ----------------------------------------------------
                case 6:
                    System.out.println("\n===== STATISTIQUES =====");
                    System.out.print("Entrez le constructeur : ");
                    String fabStat = sc.nextLine().trim();
                    List<Avion> avionsFilter = service.searchByFabricant(fabStat);

                    if (!avionsFilter.isEmpty()) {
                        Avion[] tabFilter = avionsFilter.toArray(new Avion[0]);

                        // Moyennes
                        System.out.println("Moyenne crashs : " + nativeLib.moyenneCrashs(tabFilter));
                        System.out.println("Moyenne autonomie : " + nativeLib.moyenneAutonomie(tabFilter));

                        // Top 3 avions les plus fiables (moins de crashs)
                        Avion[] topFiabilite = nativeLib.trierParCrashs(tabFilter);
                        System.out.println("\nTop 3 avions les plus fiables :");
                        for (int i = 0; i < Math.min(3, topFiabilite.length); i++) {
                            System.out.println((i + 1) + " — " + topFiabilite[i]);
                        }

                        // Top 3 avions les plus autonomes
                        Avion[] topAutonomie = nativeLib.trierParAutonomie(tabFilter);
                        System.out.println("\nTop 3 avions les plus autonomes :");
                        for (int i = topAutonomie.length - 1; i >= Math.max(0, topAutonomie.length - 3); i--) {
                            System.out.println((topAutonomie.length - i) + " — " + topAutonomie[i]);
                        }

                        // Pire avion (plus de crashs)
                        Avion pireAvion = topFiabilite[topFiabilite.length - 1];
                        System.out.println("\nPire avion en termes de sécurité :");
                        System.out.println("Modèle : " + pireAvion.getModele() + ", Nombre de crashs : " + pireAvion.getCrashs());

                    } else {
                        System.out.println("Aucun avion trouvé pour ce constructeur.");
                    }
                    break;

                // ----------------------------------------------------
                // 0 — Quitter
                // ----------------------------------------------------
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
