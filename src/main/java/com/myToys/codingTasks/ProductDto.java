package com.myToys.codingTasks;

import java.io.Serializable;

public class ProductDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String price;
	private String oldPrice;
	private String stock;
	private String brand;

	public ProductDto(String id, String name, String price, String oldPrice, String stock, String brand) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.oldPrice = oldPrice;
		this.stock = stock;
		this.brand = brand;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		return "ProductDto [id=" + id + ", name=" + name + ", price=" + price + ", oldPrice=" + oldPrice + ", stock="
				+ stock + ", brand=" + brand + "]";
	}
}
