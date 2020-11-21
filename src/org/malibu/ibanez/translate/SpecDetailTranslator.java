package org.malibu.ibanez.translate;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public interface SpecDetailTranslator {

	TranslationReport translate(Guitar guitar, Spec parentSpec, SpecDetails specDetail);
	
}
