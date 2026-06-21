package com.ecommerce.model;

import java.sql.Timestamp;

public class UserKey {
	private int id;
	private int userId;
	private String publicKey;
	private String status;
	private Timestamp createdAt;
	private Timestamp revokedAt;
	
	public Timestamp getRevokedAt() {
		return revokedAt;
	}

	public void setRevokedAt(Timestamp revokedAt) {
		this.revokedAt = revokedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
