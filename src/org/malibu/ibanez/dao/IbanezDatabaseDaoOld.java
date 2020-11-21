package org.malibu.ibanez.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class IbanezDatabaseDaoOld implements IbanezDao {
	
	private Connection conn;
	
	public IbanezDatabaseDaoOld() throws SQLException {
		conn = getConnection();
	}
	
	public void storeGuitar(Guitar guitar) throws IbanezException {
		String insertGuitarSql = "INSERT INTO IBANEZ.GUITAR (MODEL_NAME, DESCRIPTION) VALUES (?,?)";
		String insertImageSql = "INSERT INTO IBANEZ.IMAGE (GUITAR_ID, URL) VALUES (?,?)";
		String insertSpecSql = "INSERT INTO IBANEZ.SPEC (GUITAR_ID, SPEC_TITLE) VALUES (?,?)";
		String insertSpecDetailSql = "INSERT INTO IBANEZ.SPEC_DETAIL (SPEC_ID, YEAR_PRODUCED, DESCRIPTION) VALUES (?,?,?)";
		
		try {
			int guitarId = -1;
			
			// guitar
			try(PreparedStatement stmt = conn.prepareStatement(insertGuitarSql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, guitar.getModelName());
				stmt.setString(2, guitar.getDescription());
				stmt.executeUpdate();
				
				try(ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()){
						guitarId = rs.getInt(1);
					}
				}
				
				if(guitarId == -1) {
					throw new SQLException("Guitar ID was not generated (or couldn't be retrieved)");
				}
			}
			
			// image
			try(PreparedStatement stmt = conn.prepareStatement(insertImageSql)) {
				for(String imgUrl : guitar.getImageUrls()) {
					stmt.setInt(1, guitarId);
					stmt.setString(2, imgUrl);
					stmt.executeUpdate();
				}
			}
			
			// spec
			try(PreparedStatement stmt = conn.prepareStatement(insertSpecSql, Statement.RETURN_GENERATED_KEYS)) {
				for(Spec spec : guitar.getSpecs()) {
					int specId = -1;
					
					stmt.setInt(1, guitarId);
					stmt.setString(2, spec.getSpecTitle());
					stmt.executeUpdate();
					
					try(ResultSet rs = stmt.getGeneratedKeys()) {
						if (rs.next()){
							specId = rs.getInt(1);
						}
					}
					
					if(specId == -1) {
						throw new SQLException("Spec ID was not generated (or couldn't be retrieved)");
					}
					
					// spec detail
					try(PreparedStatement stmt2 = conn.prepareStatement(insertSpecDetailSql)) {
						for(SpecDetails detail : spec.getSpecDetails()) {
							List<Integer> yearsProduced = detail.getYearsProduced().isEmpty() ? Arrays.asList((Integer)null) : detail.getYearsProduced();
							for(Integer year : yearsProduced) {
								stmt2.setInt(1, specId);
								if(year == null) {
									stmt2.setNull(2, Types.INTEGER);
								} else {
									stmt2.setInt(2, year);
								}
								stmt2.setString(3, detail.getDescription());
								stmt2.executeUpdate();
							}
						}
					}
				}
			}
		} catch (SQLException | RuntimeException ex) {
			throw new IbanezException("Failed to store guitar " + guitar.getModelName(), ex);
		}
	}
	
	private Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/ibanez", "root", "blah55");
		} catch (ClassNotFoundException ex) {
			throw new SQLException("Failed to load driver class");
		}
	}
	
	@Override
	public void close() throws Exception {
		conn.close();
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
