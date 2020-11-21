package org.malibu.ibanez;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.malibu.text.util.Ascii;

public class AsciiChecker {
	
	public static void main(String[] args) throws IOException {
		File inputFile = new File("C:\\Users\\ampp3\\Desktop\\guitars.txt");
		File outputFile = new File("C:\\Users\\ampp3\\Desktop\\ascii.txt");
		AsciiChecker.generateReportOfUnprintableAsciiCharacters(inputFile, outputFile);
	}
	
	public static void generateReportOfUnprintableAsciiCharacters(File inputFile, File reportOutputFile) throws IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(reportOutputFile))) {
			int lineNumber = 0;
			String line = null;
			while((line = reader.readLine()) != null) {
				lineNumber++;
				for(char ch : line.toCharArray()) {
					if(!Ascii.isAsciiPrintable(ch)) {
						writer.write(lineNumber + ": " + ch);
						writer.newLine();
					}
				}
			}
			System.out.println("Done processing!");
		}
	}
	
	public static String getUnprintableAsciiCharacters(String textToAnalyze) {
		if(textToAnalyze != null) {
			Set<Character> unprintableChars = new HashSet<>();
			for(char ch : textToAnalyze.toCharArray()) {
				if(!Ascii.isAsciiPrintable(ch) && '\n' != ch && '\t' != ch) {
					unprintableChars.add(ch);
				}
			}
			return Arrays.toString(unprintableChars.toArray());
		}
		return null;
	}

}
