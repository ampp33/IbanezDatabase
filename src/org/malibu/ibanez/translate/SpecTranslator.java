package org.malibu.ibanez.translate;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;

public interface SpecTranslator {
	
	TranslationReport translate(Guitar guitar, Spec spec);

}
