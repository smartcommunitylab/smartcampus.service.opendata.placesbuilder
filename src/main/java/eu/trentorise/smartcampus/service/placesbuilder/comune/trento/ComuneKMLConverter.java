package eu.trentorise.smartcampus.service.placesbuilder.comune.trento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import smartcampus.service.placesbuilder.data.message.Placesbuilder.Place;

public class ComuneKMLConverter {

	private final static String INPUT = "http://webapps.comune.trento.it/cartografia/catalogo?db=base&sc=commercio&ly=civici_pubblici_esercizi&fr=kml";

	private static Logger log = Logger.getLogger(ComuneKMLConverter.class);

	public static List<Place> readEserciziPubblici() throws Exception {
		String output = System.getProperty("java.io.tmpdir") + "/civici_pubblici_esercizi.csv";
		return readEserciziPubblici(INPUT, output, "smartcampus.service.placesbuilder");
	}

	private static List<Place> readEserciziPubblici(String input, String output, String serviceId) throws Exception {
		File f;
		if (input.startsWith("http://")) {
			f = download(input);
		} else {
			f = new File(input);
		}

		ZipFile zf = new ZipFile(f);

		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zf.entries();
		List<Place> places = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			places = parseXML(zf.getInputStream(entry));

			StringBuilder sb = new StringBuilder();
			for (Place place : places) {
				String csv = String.format("%s@serviceId;smart;;%s;%s;WGS84;ITA;Italy;TN;%s;38100;%s %s;en;Food\n", WordUtils.capitalize(place.getName().toLowerCase()), place.getLatitude(), place.getLongitude(), place.getTown(), WordUtils.capitalize(place.getStreet().toLowerCase()), place.getNumber());
				sb.append(csv);
			}

			log.info("Writing CSV to: " + output);
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(sb.toString().getBytes());

		}

		return places;
	}

	private static File download(String address) throws Exception {
		String tmp = System.getProperty("java.io.tmpdir");
		File f = new File(tmp, "tmpzip.zip");
		FileUtils.copyURLToFile(new URL(address), f);
		return f;
	}

	private static List<Place> parseXML(InputStream is) throws Exception {
		List<Place> result = new ArrayList<Place>();

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().compile("//Placemark").evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			Place.Builder builder = Place.newBuilder();

			NodeList attributes = (NodeList) XPathFactory.newInstance().newXPath().compile("ExtendedData/SchemaData/SimpleData").evaluate(element, XPathConstants.NODESET);
			for (int j = 0; j < attributes.getLength(); j++) {
				String attr = ((Element) attributes.item(j)).getAttribute("name");
				String value = ((Element) attributes.item(j)).getTextContent();
				if ("peinse".equals(attr)) {
					String name = value.trim();
					builder.setName(name);
				} else if ("desvia".equals(attr)) {
					builder.setStreet(value.trim());
				} else if ("civico_alf".equals(attr)) {
					builder.setNumber(value.trim());
				}
			}

			attributes = (NodeList) XPathFactory.newInstance().newXPath().compile("//Point/coordinates").evaluate(element, XPathConstants.NODESET);
			for (int j = 0; j < attributes.getLength(); j++) {
				String attr = ((Element) attributes.item(j)).getAttribute("name");
				String value = ((Element) attributes.item(j)).getTextContent();
				String lonlat[] = value.split(",");
				// swapped
				builder.setLongitude(Double.parseDouble(lonlat[0]));
				builder.setLatitude(Double.parseDouble(lonlat[1]));
			}

			builder.setTown("Trento");
			result.add(builder.build());
		}

		return result;
	}

}
