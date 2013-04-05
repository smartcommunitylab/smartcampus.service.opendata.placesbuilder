package eu.trentorise.smartcampus.service.placesbuilder;

import junit.framework.TestCase;
import eu.trentorise.smartcampus.service.placesbuilder.comune.trento.ComuneKMLConverter;

public class TestProcess extends TestCase {
	
	public void test() throws Exception {
		ComuneKMLConverter c = new ComuneKMLConverter();
		c.readEserciziPubblici();
	}

}
