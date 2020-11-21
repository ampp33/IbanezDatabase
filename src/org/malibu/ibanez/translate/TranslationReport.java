package org.malibu.ibanez.translate;

public class TranslationReport {
	
	private boolean guitarUpdated;
	private boolean specUpdated;
	private boolean specListModified;
	private boolean specDetailUpdated;
	private boolean specDetailListModified;
	
	public boolean isGuitarUpdated() {
		return guitarUpdated;
	}
	
	public TranslationReport guitarUpdated(boolean guitarUpdated) {
		this.guitarUpdated = guitarUpdated;
		return this;
	}
	
	public boolean isSpecUpdated() {
		return specUpdated;
	}
	
	public TranslationReport specUpdated() {
		this.specUpdated = true;
		return this;
	}
	
	public boolean isSpecListModified() {
		return specListModified;
	}
	
	public TranslationReport specListModified() {
		this.specListModified = true;
		return this;
	}

	public boolean isSpecDetailUpdated() {
		return specDetailUpdated;
	}
	
	public TranslationReport specDetailUpdated() {
		this.specDetailUpdated = true;
		return this;
	}

	public boolean isSpecDetailListModified() {
		return specDetailListModified;
	}
	
	public TranslationReport specDetailListModified() {
		this.specDetailListModified = true;
		return this;
	}
	
}
