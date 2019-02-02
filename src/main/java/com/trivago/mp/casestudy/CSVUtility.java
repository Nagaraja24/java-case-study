package com.trivago.mp.casestudy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

/**
 * 
 * Utility class for CSV file parsing
 * 
 * @author nagaraj
 *
 */
public class CSVUtility {

	private static final Logger LOG = LoggerFactory.getLogger(CSVUtility.class);
	private static final CharSequence CSV_DATA_SPERATOR = "|";
	private static final String PIPE_DELIM = "\\|";
	// CSV column indexes start
	private static final int ADVERTISER_ID = 0;
	private static final int ADVERTISER_NAME = 1;
	private static final int HOTEL_ID = 0;
	private static final int HOTEL_NAME = 4;
	private static final int HOTEL_RATING = 5;
	private static final int HOTEL_STARS = 6;
	private static final int CITY_ID = 0;
	private static final int CITY_NAME = 1;
	private static final int HOTEL_CITY_ID = 1;
	// CSV column indexes end

	private CSVUtility() {
		// Do not allow to create an object
	}

	public static List<String> parseCSVFile(final String filePath) {
		LOG.info("Entred in to parseCSVFile()");
		LOG.info("File path is : {}", filePath);

		List<String> dataList = new ArrayList<>();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(filePath));
			String[] nextLine;
			int lineNumber = 0;
			while ((nextLine = csvReader.readNext()) != null) {
				lineNumber++;
				if (lineNumber == 1)
					continue;
				String data = String.join(CSV_DATA_SPERATOR, nextLine);
				LOG.info("The data is {}", data);
				dataList.add(data);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("CSV File not found, please check the file paths");
		} catch (IOException e) {
			throw new RuntimeException("CSV File parse exception, please check the file format");
		} finally {
			try {
				if (csvReader != null)
					csvReader.close();
			} catch (IOException e) {
				LOG.error("Exception occured while closing reader: {}", e.getLocalizedMessage());
			}
		}

		LOG.info("Exit from parseCSVFile()");
		return dataList;
	}

	public static Advertiser mapCSVToAdvertiser(String csvStringData) {
		String[] csvData = csvStringData.split(PIPE_DELIM);
		int id = Integer.valueOf(csvData[ADVERTISER_ID].trim());
		String name = csvData[ADVERTISER_NAME].trim();

		return new Advertiser(id, name);

	}

	public static HotelWithCityId mapCSVToHotel(String csvStringData) {
		String[] csvData = csvStringData.split(PIPE_DELIM);
		int id = Integer.valueOf(csvData[HOTEL_ID].trim());
		int city_id = Integer.valueOf(csvData[HOTEL_CITY_ID].trim());
		String name = csvData[HOTEL_NAME].trim();
		int rating = Integer.valueOf(csvData[HOTEL_RATING].trim());
		int stars = Integer.valueOf(csvData[HOTEL_STARS].trim());

		return new HotelWithCityId(id, name, rating, stars, city_id);

	}

	public static City mapCSVToCity(String csvStringData) {
		String[] csvData = csvStringData.split(PIPE_DELIM);
		int id = Integer.valueOf(csvData[CITY_ID].trim());
		String name = csvData[CITY_NAME].trim();

		return new City(id, name);

	}

	public static HotelAdvertiser mapCSVToHotelAdvertiser(String csvStringData) {
		String[] csvData = csvStringData.split(PIPE_DELIM);
		int hotel_id = Integer.valueOf(csvData[0].trim());
		int advertiser_id = Integer.valueOf(csvData[1].trim());

		return new HotelAdvertiser(advertiser_id, hotel_id);

	}

}
