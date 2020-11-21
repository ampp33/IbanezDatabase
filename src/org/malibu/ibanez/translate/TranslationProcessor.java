package org.malibu.ibanez.translate;

import java.util.ArrayList;
import java.util.List;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class TranslationProcessor {
	
	private List<GuitarTranslator> guitarTranslators = new ArrayList<>();
	private List<SpecTranslator> specTranslators  = new ArrayList<>();
	private List<SpecDetailTranslator> specDetailTranslators = new ArrayList<>();
	
	public void translate(Guitar guitar) {
		// guitar translation
		for(GuitarTranslator guitarTranslator : guitarTranslators) {
			guitarTranslator.translate(guitar);
		}
		
		// spec translation
		boolean specsNeedTranslating = true;
		while(specsNeedTranslating) {
			specsNeedTranslating = false;
			for (Spec spec : guitar.getSpecs()) {
				for(SpecTranslator specTranslator : specTranslators) {
					specsNeedTranslating |= specTranslator.translate(guitar, spec).isSpecListModified();
				}
				// if list of specs were modified during spec translation then halt processing and
				// re-translate all specs to avoid list modification errors
				if(specsNeedTranslating) {
					break;
				}
				
				// spec detail translation
				boolean specDetailsNeedTranslating = true;
				while(specDetailsNeedTranslating) {
					specDetailsNeedTranslating = false;
					for (SpecDetails specDetail : spec.getSpecDetails()) {
						for(SpecDetailTranslator specDetailTranslator : specDetailTranslators) {
							TranslationReport translationReport = specDetailTranslator.translate(guitar, spec, specDetail);
							specsNeedTranslating |= translationReport.isSpecListModified();
							specDetailsNeedTranslating |= translationReport.isSpecDetailListModified();
						}
						
						// if list of spec details were modified during translation then halt processing and
						// re-translate all spec details to avoid list modification errors
						if(specDetailsNeedTranslating) {
							break;
						}
					}
				}
				
				// if list of specs were modified during spec detail translation then halt processing and
				// re-translate all specs to avoid list modification errors
				if(specsNeedTranslating) {
					break;
				}
			}
		}
	}
	
	public void addGuitarTranslator(GuitarTranslator guitarTranslator) {
		guitarTranslators.add(guitarTranslator);
	}
	
	public void addSpecTranslator(SpecTranslator specTranslator) {
		specTranslators.add(specTranslator);
	}
	
	public void addSpecDetailTranslator(SpecDetailTranslator specDetailTranslator) {
		specDetailTranslators.add(specDetailTranslator);
	}
	
}
