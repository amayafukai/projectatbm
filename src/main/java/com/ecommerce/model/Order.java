package com.ecommerce.model;

import java.sql.Timestamp;

public class Order {
	private int id;
	private int userId;
	private int productId;
	private String productName;
	private double price;
	private int quantity;
	private String customerName;
	private String address;
	private String promotion;
	private String orderHash;
	private String signature;
	private int publicKeyId;
	private String verifyStatus;
	private String orderDetails;
	private int orderGroupId;
	
	// getter và setter
	public int getOrderGroupId() {
		return orderGroupId;
	}

	public String getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(String orderDetails) {
		this.orderDetails = orderDetails;
	}

	public void setOrderGroupId(int orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public String getOrderHash() {
		return orderHash;
	}

	public void setOrderHash(String orderHash) {
		this.orderHash = orderHash;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getPublicKeyId() {
		return publicKeyId;
	}

	public void setPublicKeyId(int publicKeyId) {
		this.publicKeyId = publicKeyId;
	}

	public String getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	// Thêm getter/setter cho tất cả
	// Getters and Setters (Generate them all)
	// Example:
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

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private Timestamp orderDate;

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

}
