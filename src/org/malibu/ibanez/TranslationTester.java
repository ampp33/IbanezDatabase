package org.malibu.ibanez;

import java.util.List;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.dao.IbanezDao;
import org.malibu.ibanez.dao.IbanezDatabaseDao;
import org.malibu.ibanez.translate.BodyMaterialTranslator;
import org.malibu.ibanez.translate.TranslationProcessor;

public class TranslationTester {
	
	public static void main(String[] args) throws Exception {
		TranslationProcessor translationProcessor = new TranslationProcessor();
		translationProcessor.addSpecDetailTranslator(new BodyMaterialTranslator());
		try(IbanezDao stagingDao = new IbanezDatabaseDao("ibanez")) {
			List<Guitar> allGuitars = stagingDao.retrieveAllGuitars();
			for (Guitar guitar : allGuitars) {
				System.out.println(guitar.toString());
				System.out.println("->");
				translationProcessor.translate(guitar);
				System.out.println(guitar.toString());
			}
		}
		
	}

}
