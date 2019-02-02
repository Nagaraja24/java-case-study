package com.trivago.mp.casestudy;

public class HotelAdvertiser {

	private final int advertiser_id;
	private final int hotel_id;

	public HotelAdvertiser(int advertiser_id, int hotel_id) {
		this.advertiser_id = advertiser_id;
		this.hotel_id = hotel_id;
	}

	public int getAdvertiser_id() {
		return advertiser_id;
	}

	public int getHotel_id() {
		return hotel_id;
	}

}
