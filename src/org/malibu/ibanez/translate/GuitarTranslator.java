package org.malibu.ibanez.translate;

import org.malibu.ibanez.api.Guitar;

public interface GuitarTranslator {
	
	TranslationReport translate(Guitar guitar);

}
