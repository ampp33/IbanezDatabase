package org.malibu.ibanez.translate;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class MaterialTranslator implements SpecDetailTranslator {
	
	// Allowed modifiers
	// Curly
	// African
	// Hawaiian
	// Burl
	// Flamed - Flame
	// Macassar
	// Quilted
	// Indian
	// Spalted
	// Swamp
	// Exotic
	// Birds Eye
	// Buckeye
	// Figured
	// Gravure
	
	// Allowed wood types
	// Maple
	// Mahogany
	// Okoume
	// Spruce
	// Catalpa
	// Cocobolo
	// Sycamore
	// Linden
	// Koa
	// Ebony
	// Meranti
	// Nyatoh
	// Sapele
	// Ovangkol
	// Pau Ferro
	// Rosewood
	// Zebrawood
	// Ziricote
	// Ash
	// Poplar
	// Myrtle
	// Resin
	// Marblewood
	// Richlite
	// Agathis
	// Alder
	// Basswood
	// Claro Walnut
	// Birch
	// Veneer
	// Walnut
	// Sonokeling
	
	// Do combos like this: modifier + wood, wood + modifier
	
	// Ignore terms after 'with'?
	
	// Need another translator for binding types, since "white" isn't a type of wood?
	
	// BINDING ONLY
	
	// White
	// Natural
	// Just "Binding"
	// Any of the above combinations + Binding
	// Ivory
	// Pearloid

	
	private static final String SPLIT_TOKENS = "w/|with|and|\\&";
	
	private TranslationReport report = null;

	@Override
	public TranslationReport translate(Guitar guitar, Spec parentSpec, SpecDetails specDetail) {
		report = new TranslationReport();
		// null checks
		if(parentSpec != null && isMaterialSpec(parentSpec.getSpecTitle())
				&& specDetail != null && specDetail.getDescription() != null) {
			// split spec detail text by known separator tokens
			String[] detailTokens = specDetail.getDescription().split(SPLIT_TOKENS);
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isMaterialSpec(String specTitle) {
		
	}

}
