package org.malibu.ibanez.dao;

import java.util.List;

import org.malibu.ibanez.api.Guitar;

public interface IbanezDao extends AutoCloseable {
	
	Guitar retrieveGuitar(String modelName) throws IbanezException;
	
	List<Guitar> retrieveAllGuitars() throws IbanezException;
	
	void storeGuitar(Guitar guitar) throws IbanezException;

}
