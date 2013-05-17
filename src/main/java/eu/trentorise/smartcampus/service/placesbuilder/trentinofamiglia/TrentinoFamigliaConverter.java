package eu.trentorise.smartcampus.service.placesbuilder.trentinofamiglia;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import smartcampus.service.placesbuilder.data.message.Placesbuilder.Place;
import eu.trentorise.smartcampus.service.placesbuilder.comune.trento.ComuneKMLConverter;

public class TrentinoFamigliaConverter {

	private static Logger log = Logger.getLogger(ComuneKMLConverter.class);

	public static List<Place> readOrganizzazioni(String s) throws Exception {
		String output = System.getProperty("java.io.tmpdir")
				+ "/trentino_famiglia_organizzazioni.csv";
		return readOrganizzazioni(s, output,
				"smartcampus.service.placesbuilder");
	}

	private static List<Place> readOrganizzazioni(String s, String output,
			String serviceId) throws Exception {
		List<Place> places = parseCSV(s);

		StringBuilder sb = new StringBuilder();
		for (Place place : places) {
			String csv = String
					.format("%s@serviceId;smart;;%s;%s;WGS84;ITA;Italy;%s;%s;;%s %s;en;Organization\n",
							WordUtils.capitalize(place.getName().toLowerCase()),
							place.getLatitude(), place.getLongitude(), place
									.getProvince(), place.getTown(),
							WordUtils.capitalize(place.getStreet()
									.toLowerCase()), place.getNumber());
			sb.append(csv.replace(" ;", ";"));
		}

		log.info("Writing CSV to: " + output);
		FileOutputStream fos = new FileOutputStream(output);
		fos.write(sb.toString().getBytes());

		return places;
	}

	private static List<Place> parseCSV(String s) throws Exception {
		List<Place> result = new ArrayList<Place>();
		try {
			BufferedReader br = new BufferedReader(new StringReader(s));
			String line = null;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
					continue;
				}
				String words[] = line.replace("\"", "").split(";");

				Place.Builder builder = Place.newBuilder();
				builder.setName(removeSpaces(words[1]));
				
				String address[] = words[3].split(" ");
				String street = null;
				String number = address[address.length - 1];
				String n = null;
				
				if (!number.replaceAll("[\\d]", "").equals(number)) {
					n = number;
				}
				
				if (n != null) {
					street = words[3].replace(number, "").trim();
					builder.setNumber(number);					
				} else {
					street = words[3];
				}
				builder.setStreet(removeSpaces(street));
				
				builder.setTown(removeSpaces(words[4]));
				builder.setProvince(removeSpaces(words[5]));
				builder.setLatitude(Double
						.parseDouble(transformLatLong(words[6])));
				builder.setLongitude(Double
						.parseDouble(transformLatLong(words[7])));
				result.add(builder.build());
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static String removeSpaces(String s) {
		return s.replaceAll("[\\s]+", " ").trim();
	}

	private static String transformLatLong(String ll) {
		String s = ll.replaceFirst("\\.", ",").replaceFirst("\\.", "")
				.replaceAll(",", ".");
		return s;
	}

}
