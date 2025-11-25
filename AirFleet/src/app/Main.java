package app;

import java.util.List;
import java.util.Scanner;
import model.Avion;
import service.AvionService;

public class Main {

    public static void main(String[] args) {

        AvionService service = new AvionService();
        Scanner sc = new Scanner(System.in);

        int choix;

        do {
            System.out.println("\n===== BASE DE DONNÉES AVIONS =====");
            System.out.println("1 — Afficher tous les avions");
            System.out.println("2 — Rechercher un avion par modèle");
            System.out.println("3 — Rechercher par fabricant");
            System.out.println("4 — Ajouter un avion");
            System.out.println("5 — Supprimer un avion");
            System.out.println("0 — Quitter");
            System.out.print("Votre choix : ");

            choix = Integer.parseInt(sc.nextLine());

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
                    String modele = sc.nextLine();
                    Avion avion = service.searchByModel(modele);

                    if (avion != null) System.out.println("\n" + avion);
                    else System.out.println("Aucun avion trouvé avec ce modèle.");
                    break;

                // ----------------------------------------------------
                // 3 — Recherche par fabricant
                // ----------------------------------------------------
                case 3:
                    System.out.print("Fabricant (Airbus/Boeing) : ");
                    String fabricant = sc.nextLine();
                    List<Avion> fabList = service.searchByFabricant(fabricant);

                    if (!fabList.isEmpty()) fabList.forEach(System.out::println);
                    else System.out.println("Aucun avion trouvé pour ce fabricant.");
                    break;

                // ----------------------------------------------------
                // 4 — Ajouter un avion
                // ----------------------------------------------------
                case 4:
                    System.out.print("Fabricant : ");
                    String fab = sc.nextLine();

                    System.out.print("Modèle : ");
                    String mod = sc.nextLine();

                    System.out.print("Capacité : ");
                    int cap = Integer.parseInt(sc.nextLine());

                    System.out.print("Autonomie (km) : ");
                    int auto = Integer.parseInt(sc.nextLine());

                    System.out.print("Nombre de crashs : ");
                    int crash = Integer.parseInt(sc.nextLine());

                    System.out.print("Année d'entrée en service : ");
                    int year = Integer.parseInt(sc.nextLine());

                    Avion newAvion = new Avion(0, fab, mod, cap, auto, crash, year);
                    service.add(newAvion);
                    System.out.println("Avion ajouté avec succès !");
                    break;

                // ----------------------------------------------------
                // 5 — Supprimer
                // ----------------------------------------------------
                case 5:
                    System.out.print("ID de l’avion à supprimer : ");
                    int id = Integer.parseInt(sc.nextLine());
                    boolean ok = service.delete(id);

                    if (ok) System.out.println("Suppression réussie !");
                    else System.out.println("Échec de la suppression (ID introuvable).");
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
