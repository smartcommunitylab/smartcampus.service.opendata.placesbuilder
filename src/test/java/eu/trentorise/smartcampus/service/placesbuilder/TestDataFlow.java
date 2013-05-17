package eu.trentorise.smartcampus.service.placesbuilder;

import it.sayservice.platform.core.bus.common.AppConfig;
import it.sayservice.platform.servicebus.test.DataFlowTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.mvel2.sh.command.basic.Help;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import smartcampus.service.placesbuilder.data.message.Placesbuilder.Place;
import smartcampus.service.placesbuilder.impl.GetComuneTrentoPubbliciEserciziDataFlow;
import smartcampus.service.placesbuilder.impl.GetTrentinoFamigliaOrganizzazioniDataFlow;

public class TestDataFlow extends TestCase {

	public void test() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"test-context.xml");
			AppConfig ac = (AppConfig) context.getBean("appConfig");

			Map<String, Object> pars = new HashMap<String, Object>();

			DataFlowTestHelper helper = new DataFlowTestHelper();
			// Map<String, Object> out1 =
			// helper.executeDataFlow("smartcampus.service.placesbuilder",
			// "GetComuneTrentoPubbliciEsercizi", new
			// GetComuneTrentoPubbliciEserciziDataFlow(), pars);
			// for (Place place : (List<Place>)out1.get("data")) {
			// System.out.println(place.getPoi().getPoiId());
			// }
			// System.out.println(((List<Place>)out1.get("data")).size());

			Map<String, Object> out2 = helper.executeDataFlow(
					"smartcampus.service.placesbuilder",
					"GetTrentinoFamigliaOrganizzazioni",
					new GetTrentinoFamigliaOrganizzazioniDataFlow(), pars);
			System.out.println(((List<Place>) out2.get("data")).size());
			for (Place place : (List<Place>) out2.get("data")) {
//				System.out.println(place.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
