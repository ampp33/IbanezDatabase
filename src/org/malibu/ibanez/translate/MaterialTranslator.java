package org.malibu.ibanez.translate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	
	// Items that cannot be identified change to "Special"?
	
	// !!!!!!!!!!!!!!!!!!!!!
	// Create a new TAGS table and assign tags based on values in present in these fields, that way we don't have to CHANGE these values
	
	// NEED TO DO TWO THINGS IMPORTANT HERE
	// 1. update body material translator to break things apart appropriately
	// 2. create a tagging mechanism that ONLY looks for materials or important attributes (reverse headstock, series, etc)
	
	// BINDING ONLY
	
	// White
	// Natural
	// Just "Binding"
	// Any of the above combinations + Binding
	// Ivory
	// Pearloid
	
	// LIST OF SPECS THAT INVOLVE WOOD MATERIALS
	private static final Set<String> MATERIAL_SPECS = new HashSet<>(Arrays.asList(
		"Back",
		"Back/sides",
		"Body material",
		"Fingerboard",
		"Fingerboard inlays",
		"Fingerboard material",
		"Inlays",
		"Neck material",
		"Sides",
		"Soundhole rosette",
		"Top"
	));
	
	private static final String SPLIT_TOKENS = "w/|with|and|\\&| ";
	
	private TranslationReport report = null;
	
	private Set<String> UNIQUE_STRINGS = new HashSet<>();

	@Override
	public TranslationReport translate(Guitar guitar, Spec parentSpec, SpecDetails specDetail) {
		report = new TranslationReport();
		// null checks
		if(parentSpec != null && MATERIAL_SPECS.contains(parentSpec.getSpecTitle())
				&& specDetail != null && specDetail.getDescription() != null) {
			// split spec detail text by known separator tokens
			String[] detailTokens = specDetail.getDescription().split(SPLIT_TOKENS);
			UNIQUE_STRINGS.addAll(Arrays.asList(detailTokens));
		}
		return report;
	}
	
	public Set<String> getUniqueStrings() {
		return UNIQUE_STRINGS;
	}
	
}
