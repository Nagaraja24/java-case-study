package com.trivago.mp.casestudy;

public class HotelWithCityId extends Hotel {

	private final int city_id;

	public HotelWithCityId(int id, String name, int rating, int stars, int city_id) {
		super(id, name, rating, stars);
		this.city_id = city_id;
	}

	public int getCity_id() {
		return city_id;
	}

}
