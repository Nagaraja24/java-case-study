package com.trivago.mp.casestudy;

public class City {

	private final int id;
	private final String city_name;

	public City(int id, String city_name) {
		super();
		this.id = id;
		this.city_name = city_name;
	}

	public int getId() {
		return id;
	}

	public String getCity_name() {
		return city_name;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", city_name=" + city_name + "]";
	}

}
