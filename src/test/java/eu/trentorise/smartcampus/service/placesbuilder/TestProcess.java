package eu.trentorise.smartcampus.service.placesbuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.TestCase;
import eu.trentorise.smartcampus.service.placesbuilder.comune.trento.ComuneKMLConverter;
import eu.trentorise.smartcampus.service.placesbuilder.trentinofamiglia.TrentinoFamigliaConverter;

public class TestProcess extends TestCase {
	
	public void test() throws Exception {
		ComuneKMLConverter c1 = new ComuneKMLConverter();
		c1.readEserciziPubblici();
		
		TrentinoFamigliaConverter c2 = new TrentinoFamigliaConverter();
		
		InputStreamReader isr = new InputStreamReader((new URL("http://dati.trentino.it/it/storage/f/2013-05-16T105357/Registro_organizzazioni_certificate_Audit.csv").openStream()));
		BufferedReader br = new BufferedReader(isr);
		int c = 0;
		StringBuffer sb = new StringBuffer();
		while ((c = br.read()) != -1) {
			sb.append((char)c);
		}
		
		c2.readOrganizzazioni(sb.toString());
		
	}

}
