package com.trivago.mp.casestudy;

import static com.trivago.mp.casestudy.CSVUtility.mapCSVToAdvertiser;
import static com.trivago.mp.casestudy.CSVUtility.mapCSVToCity;
import static com.trivago.mp.casestudy.CSVUtility.mapCSVToHotelAdvertiser;
import static com.trivago.mp.casestudy.CSVUtility.parseCSVFile;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Please note that :
 * 
 * 1) Coding guidelines still need to be improved
 * 2) Comments are not added
 * 3) Loggers are completely implemented and enabled
 * 
 */
public class HotelSearchEngineImpl implements HotelSearchEngine {

	private List<HotelWithCityId> hotels;
	private Map<Integer, HotelWithCityId> hotelIdtoHotelMap;
	private List<City> cities;
	private List<Advertiser> advertisers;
	private List<HotelAdvertiser> hotelAdvertisers;
	private Map<String, Integer> cityMap;
	private Map<Integer, Map<Integer, HotelWithCityId>> cityToHotelMap;
	private Map<Integer, List<Integer>> advertiserToHotelIds;

	@Override
	public void initialize() {
		loadDataFromCSV();
	}

	@Override
	public List<HotelWithOffers> performSearch(String cityName, DateRange dateRange, OfferProvider offerProvider) {

		if (!cityMap.containsKey(cityName))
			return Collections.<HotelWithOffers>emptyList();

		// Filter hotels by city
		List<HotelWithCityId> hotelsForTheCity = cityToHotelMap.get(cityMap.get(cityName)).values().stream()
				.collect(toList());
		List<Integer> hotelIds = hotelsForTheCity.stream().map(HotelWithCityId::getId).collect(toList());

		// collection of Map [ hotelId--> single Offer]
		List<Map<Integer, Offer>> hotelToOfferMapsCollection = new ArrayList<>();

		// for each advertiser and respective hotelIds call get Offers
		advertisers.forEach((advertiser) -> {
			hotelToOfferMapsCollection.add(getOffers(advertiser, hotelIds, offerProvider, dateRange));
		});

		// merges the list of maps to single map [ hotelId -> list of offers]
		Map<Integer, List<Offer>> hotelIdtoOfferListMap = hotelToOfferMapsCollection.stream()
				.flatMap(m -> m.entrySet().stream()).collect(groupingBy(Map.Entry::getKey, Collector
						.of(ArrayList<Offer>::new, (list, item) -> list.add(item.getValue()), (left, right) -> {
							left.addAll(right);
							return left;
						})));

		List<HotelWithOffers> result = new ArrayList<>();

		// loop through each key and create HotelWithOffers object
		hotelIdtoOfferListMap.forEach((k, v) -> {
			result.add(new HotelWithOffers(hotelIdtoHotelMap.get(k)).addOffers(v));
		});

		return result;

	}

	private Map<Integer, Offer> getOffers(Advertiser advertiser, List<Integer> hotelIds, OfferProvider offerProvider,
			DateRange dateRange) {
		// filter hotel ids for a given city
		List<Integer> hotelIdsForAdvertiser = advertiserToHotelIds.get(advertiser.getId()).stream()
				.filter(id -> hotelIds.contains(id)).collect(toList());

		Map<Integer, Offer> hotelIdToOffers = offerProvider.getOffersFromAdvertiser(advertiser, hotelIdsForAdvertiser,
				dateRange);

		return hotelIdToOffers;
	}

	private void loadDataFromCSV() {

		// collect hotels as list
		hotels = new ArrayList<>();
		List<String> hotelCSVData = parseCSVFile("./data/hotels.csv");
		hotelCSVData.forEach((data) -> {
			hotels.add(CSVUtility.mapCSVToHotel(data));
		});

		// collect advertisers as list
		advertisers = new ArrayList<>();
		List<String> advertiserCSVData = parseCSVFile("./data/advertisers.csv");
		advertiserCSVData.forEach((data) -> {
			advertisers.add(mapCSVToAdvertiser(data));
		});

		// collect cities as list
		cities = new ArrayList<>();
		List<String> cityCSVData = parseCSVFile("./data/cities.csv");
		cityCSVData.forEach((data) -> {
			cities.add(mapCSVToCity(data));
		});

		// collect HotelAdvsors as list
		hotelAdvertisers = new ArrayList<>();
		List<String> hotelAdvertCSVData = parseCSVFile("./data/hotel_advertiser.csv");
		hotelAdvertCSVData.forEach((data) -> {
			hotelAdvertisers.add(mapCSVToHotelAdvertiser(data));
		});

		// map [ cityId -> cityName]
		cityMap = cities.stream().collect(toMap(City::getCity_name, City::getId));

		// map [hotelId->HotelWithCityId]
		hotelIdtoHotelMap = hotels.stream().collect(toMap(HotelWithCityId::getId, Function.identity()));

		// map [advertiserId-> list of hotelIds
		advertiserToHotelIds = hotelAdvertisers.stream().collect(
				groupingBy(HotelAdvertiser::getAdvertiser_id, mapping(HotelAdvertiser::getHotel_id, toList())));

		// map [cityId->HotelWithCityId]
		cityToHotelMap = hotels.stream()
				.collect(groupingBy(HotelWithCityId::getCity_id, toMap(HotelWithCityId::getId, Function.identity())));
	}

}
