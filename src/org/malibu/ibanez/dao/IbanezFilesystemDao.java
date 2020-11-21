package org.malibu.ibanez.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.malibu.ibanez.AsciiChecker;
import org.malibu.ibanez.api.Guitar;

public class IbanezFilesystemDao implements IbanezDao {

	private static final Object LOCK = new Object();
	private static byte[] NEWLINE_BYTES = "\n".getBytes();
	
	private FileOutputStream writer = null;

	public IbanezFilesystemDao(File storageFile) throws FileNotFoundException {
//		File file = new File("C:\\Users\\ampp3\\Desktop\\guitars.txt");
		writer = new FileOutputStream(storageFile);
	}

	public void storeGuitar(Guitar guitar) throws IbanezException {
		synchronized (LOCK) {
			try {
				String guitarText = guitar.toString();
				String unprintableChars = AsciiChecker.getUnprintableAsciiCharacters(guitarText);
				writer.write(guitarText.getBytes());
				writer.write(NEWLINE_BYTES);
				if (unprintableChars != null) {
					writer.write(("Unprintable Characters: " + unprintableChars).getBytes());
					writer.write(NEWLINE_BYTES);
					writer.write(NEWLINE_BYTES);
				}
			} catch (IOException e) {
				throw new IbanezException("Failed to store guitar " + guitar.getModelName(), e);
			}
		}
	}
	
	@Override
	public void close() throws Exception {
		writer.close();
	}

	@Override
	public List<Guitar> retrieveAllGuitars() throws IbanezException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Guitar retrieveGuitar(String modelName) throws IbanezException {
		// TODO Auto-generated method stub
		return null;
	}

}
