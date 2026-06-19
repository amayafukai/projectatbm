package com.ecommerce.dao;

import com.ecommerce.connection.DBConnection;
import com.ecommerce.model.UserKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserKeyDAO {

    // Lưu public key mới (trạng thái ACTIVE)
    public boolean savePublicKey(int userId, String publicKey) {
        String sql = "INSERT INTO user_keys(user_id, public_key, status, created_at) VALUES (?, ?, 'ACTIVE', NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, publicKey);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy public key đang hoạt động (ACTIVE) mới nhất của user
    public UserKey getActiveKey(int userId) {
        String sql = "SELECT * FROM user_keys WHERE user_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserKey key = new UserKey();
                key.setId(rs.getInt("id"));
                key.setUserId(rs.getInt("user_id"));
                key.setPublicKey(rs.getString("public_key"));
                key.setStatus(rs.getString("status"));
                key.setCreatedAt(rs.getTimestamp("created_at"));
                return key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thu hồi tất cả key ACTIVE của user (chuyển sang REVOKED)
    public boolean revokeAllKeys(int userId) {
        String sql = "UPDATE user_keys SET status = 'REVOKED', revoked_at = NOW() WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}