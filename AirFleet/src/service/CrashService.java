package service;

import database.Database;
import model.Crash;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrashService {
	
	public List<Crash> getAll() {
        List<Crash> crashs = new ArrayList<>();
        String sql = "SELECT * FROM crash";
        try (Connection conn = Database.getConnection(); //connection a la bd
             Statement stmt = conn.createStatement(); // permet d'executer les requetes
             ResultSet rs = stmt.executeQuery(sql)) { //permet de "stocker" le resultat de la query

            while (rs.next()) {
                Crash a = new Crash(
                		 rs.getInt("id"),
                         rs.getInt("avion_id"),
                         rs.getString("modele"),
                         rs.getDate("date_crash"),
                         rs.getString("lieu"),
                         rs.getString("gravite"),
                         rs.getInt("morts"),
                         rs.getInt("blesses"),
                         rs.getString("cause"),
                         rs.getString("description")

                );
                crashs.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crashs;
    }
	
    // Récupérer tous les crashs pour un fabricant
    public List<Crash> getByFabricant(String fabricant) {
        List<Crash> res = new ArrayList<>();
        String sql = "SELECT c.*, a.modele FROM crash c JOIN avions a ON c.avion_id = a.id " +
                     "WHERE a.fabricant = ? ORDER BY c.date_crash DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fabricant);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Crash(
                        rs.getInt("id"),
                        rs.getInt("avion_id"),
                        rs.getString("modele"),
                        rs.getDate("date_crash"),
                        rs.getString("lieu"),
                        rs.getString("gravite"),
                        rs.getInt("morts"),
                        rs.getInt("blesses"),
                        rs.getString("cause"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // Récupérer tous les crashs pour un modèle précis
    public List<Crash> getByModele(String modele) {
        List<Crash> res = new ArrayList<>();
        String sql = "SELECT c.*, a.modele FROM crash c JOIN avions a ON c.avion_id = a.id WHERE a.modele = ? ORDER BY c.date_crash DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modele);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                res.add(new Crash(
                        rs.getInt("id"),
                        rs.getInt("avion_id"),
                        rs.getString("modele"),
                        rs.getDate("date_crash"),
                        rs.getString("lieu"),
                        rs.getString("gravite"),
                        rs.getInt("morts"),
                        rs.getInt("blesses"),
                        rs.getString("cause"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // Ajouter un crash
    public void addCrash(Crash c) {
        String sql = "INSERT INTO crash (avion_id, modele, date_crash, lieu, gravite, morts, blesses, cause, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getAvionId());
            ps.setString(2, c.getModele());
            ps.setDate(3, c.getDateCrash());
            ps.setString(4, c.getLieu());
            ps.setString(5, c.getGravite());
            ps.setInt(6, c.getMorts());
            ps.setInt(7, c.getBlesses());
            ps.setString(8, c.getCause());
            ps.setString(9, c.getDescription());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Supprimer un crash
    public boolean deleteCrash(int id) {
        String sql = "DELETE FROM crash WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Nombre de crashs pour un avion
    public int countByAvion(int avionId) {
        String sql = "SELECT crashs as nb FROM avions WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, avionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("nb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Total morts par fabricant
    public int totalMortsParFabricant(String fabricant) {
        String sql = "SELECT SUM(c.morts) as s FROM crash c JOIN avions a ON c.avion_id = a.id WHERE a.fabricant = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fabricant);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("s");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // nombre de crashs par constructeur 
    public int countByConstructeur(String fabricant) {
        String sql = "SELECT sum(crashs) as nb FROM avions where fabricant = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fabricant);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("nb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}


