package service;

import database.Database;
import model.Avion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    
    public List<String> getAllConstructeurs() {
        List<String> constructeurs = new ArrayList<>();
        // On suppose que tu as une méthode getConnection() pour JDBC
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
    
    public List<String> getAllModele() {
        List<String> modele = new ArrayList<>();
        // On suppose que tu as une méthode getConnection() pour JDBC
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

}



