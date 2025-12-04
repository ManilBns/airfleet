package service;

import database.Database;
import model.Avion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class AvionService {
    public List<Avion> getAll() {
        List<Avion> avions = new ArrayList<>();
        String sql = "SELECT * FROM avions";
        try (Connection conn = Database.getConnection(); //connection a la bd
             Statement stmt = conn.createStatement(); // permet d'executer les requetes
             ResultSet rs = stmt.executeQuery(sql)) { //permet de "stocker" le resultat de la query
            while (rs.next()) {
                Avion a = new Avion(
                        rs.getInt("id"),
                        rs.getString("fabricant"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getInt("autonomie"),
                        rs.getInt("crashs"),
                        rs.getInt("annee_service")
                );
                avions.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avions;
    }

    // -----------------------------
    // Recherche par modèle
    // -----------------------------
    public Avion searchByModel(String modele) {
        String sql = "SELECT * FROM avions WHERE modele = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modele);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Avion(
                        rs.getInt("id"),
                        rs.getString("fabricant"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getInt("autonomie"),
                        rs.getInt("crashs"),
                        rs.getInt("annee_service")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // -----------------------------
    // Recherche par fabricant
    // -----------------------------
    public List<Avion> searchByFabricant(String fabricant) {
        List<Avion> avions = new ArrayList<>();
        String sql = "SELECT * FROM avions WHERE fabricant = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fabricant);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {  // <-- boucle pour tous les avions
                Avion a = new Avion(
                        rs.getInt("id"),
                        rs.getString("fabricant"),
                        rs.getString("modele"),
                        rs.getInt("capacite"),
                        rs.getInt("autonomie"),
                        rs.getInt("crashs"),
                        rs.getInt("annee_service")
                );
                avions.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return avions; // retourne la liste complète
    }
    // -----------------------------
    // Ajout d'un avion
    // -----------------------------
    public void add(Avion avion) {
        String sql = "INSERT INTO avions (fabricant, modele, capacite, autonomie, crashs, annee_service) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, avion.getFabricant());
            ps.setString(2, avion.getModele());
            ps.setInt(3, avion.getCapacite());
            ps.setInt(4, avion.getAutonomie());
            ps.setInt(5, avion.getCrashs());
            ps.setInt(6, avion.getAnneeService());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------
    // Suppression d'un avion
    // -----------------------------
    public boolean delete(int id) {
        String sql = "DELETE FROM avions WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    //avoir tous les constructeurs
    public List<String> getAllConstructeurs() {
        List<String> constructeurs = new ArrayList<>();
        String sql = "SELECT DISTINCT fabricant FROM avions";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                constructeurs.add(rs.getString("fabricant"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return constructeurs;
    }
    
    //avoir tous les constructeurs
    public List<String> getAllModele() {
        List<String> modele = new ArrayList<>();
        String sql = "SELECT DISTINCT modele FROM avions";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                modele.add(rs.getString("modele"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modele;
    }
    
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
        System.out.println("0. Retour au menu principal");

        int choix = -1;
        do {
            System.out.print("Votre choix (numéro) : ");
            String line = sc.nextLine().trim();
            try {
                choix = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                choix = -1;
            }

            if (choix == 0) return null; // retourne au menu principal

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
        System.out.println("0. Retour au menu principal");

        int choix = -1;
        do {
            System.out.print("Votre choix (numéro) : ");
            String line = sc.nextLine().trim();
            try {
                choix = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                choix = -1;
            }

            if (choix == 0) return null; // retourne au menu principal

        } while (choix < 1 || choix > modeles.size());

        return modeles.get(choix - 1);
    }
    
 // selectionner les modeles en fonction du fabricant et les afficher
    public static String choisirModelePourConstructeur(AvionService avionService, Scanner sc, String fabricant) {
        List<String> modeles = new ArrayList<>();
        String sql = "SELECT DISTINCT modele FROM avions WHERE fabricant = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fabricant);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modeles.add(rs.getString("modele"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            return null;
        }

        if (modeles.isEmpty()) {
            System.out.println("Aucun modèle existant pour " + fabricant);
            return null;
        }

        // Affichage des modèles existants pour le constructeur
        System.out.println("Modèles existants pour " + fabricant + " :");
        for (int i = 0; i < modeles.size(); i++) {
            System.out.println((i + 1) + ". " + modeles.get(i));
        }
        System.out.println("0. Retour au menu principal");

        while (true) {
            System.out.print("Votre choix : ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("menu") || input.equals("0")) {
                return null; // Retour menu
            }

            try {
                int choix = Integer.parseInt(input);
                if (choix >= 1 && choix <= modeles.size()) {
                    return modeles.get(choix - 1);
                } else {
                    System.out.println("Numéro incorrect. Réessayez.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Numéro incorrect. Réessayez.");
            }
        }
    }
 // Retourne la liste des modèles d'un constructeur donné
    public List<String> getModelesByFabricant(String fabricant) {
        List<String> modeles = new ArrayList<>();
        String sql = "SELECT DISTINCT modele FROM avions WHERE fabricant = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fabricant);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeles.add(rs.getString("modele"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modeles;
    }

}



