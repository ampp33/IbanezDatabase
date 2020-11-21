package org.malibu.ibanez.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class IbanezDatabaseDao implements IbanezDao {
	
	private Connection conn;
	
	public IbanezDatabaseDao(String schema) throws SQLException {
		conn = getConnection(schema);
	}
	
	public Guitar retrieveGuitar(String modelName) throws IbanezException {
		String selectGuitarSql = "SELECT GUITAR_ID, MODEL_NAME, DESCRIPTION FROM GUITAR WHERE MODEL_NAME = ?";
		
		Guitar guitar = null;
		
		try(PreparedStatement guitarStmt = conn.prepareStatement(selectGuitarSql)) {
			guitarStmt.setString(1, modelName);
			try(ResultSet guitarRs = guitarStmt.executeQuery()) {
				while(guitarRs.next()) {
					guitar = new Guitar();
					guitar.setModelName(guitarRs.getString("MODEL_NAME"));
					guitar.setDescription(guitarRs.getString("DESCRIPTION"));
					int guitarId = guitarRs.getInt("GUITAR_ID");
					
					populateGuitarSpecs(guitar, guitarId);
				}
			}
		} catch (SQLException | RuntimeException ex) {
			throw new IbanezException("Failed to retrieve guitar " + modelName, ex);
		}
		
		return guitar;
	}
	
	public List<Guitar> retrieveAllGuitars() throws IbanezException {
		String selectGuitarSql = "SELECT GUITAR_ID, MODEL_NAME, DESCRIPTION FROM GUITAR";
		
		List<Guitar> guitars = new LinkedList<>();
		
		try(PreparedStatement guitarStmt = conn.prepareStatement(selectGuitarSql);
				ResultSet guitarRs = guitarStmt.executeQuery()) {
			while(guitarRs.next()) {
				Guitar guitar = new Guitar();
				guitar.setModelName(guitarRs.getString("MODEL_NAME"));
				guitar.setDescription(guitarRs.getString("DESCRIPTION"));
				guitars.add(guitar);
				int guitarId = guitarRs.getInt("GUITAR_ID");
				
				populateGuitarSpecs(guitar, guitarId);
			}
		} catch (SQLException | RuntimeException ex) {
			throw new IbanezException("Failed to retrieve all guitars", ex);
		}
		
		return guitars;
	}

	private void populateGuitarSpecs(Guitar guitar, int guitarId) throws SQLException {
		String selectSpecsAndDetails = "SELECT S.SPEC_NAME, SD.DESCRIPTION, GS.YEAR_PRODUCED FROM GUITAR_SPEC GS"
											+ " JOIN SPEC S ON S.SPEC_ID = GS.SPEC_ID"
											+ " JOIN SPEC_DETAIL SD ON SD.SPEC_DETAIL_ID = GS.SPEC_DETAIL_ID"
											+ " WHERE GS.GUITAR_ID = ?";
		
		try(PreparedStatement specAndDetailStmt = conn.prepareStatement(selectSpecsAndDetails)) {
			specAndDetailStmt.setInt(1, guitarId);
			try(ResultSet specRs = specAndDetailStmt.executeQuery()) {
				Spec currentSpec = new Spec();
				SpecDetails currentSpecDetails = new SpecDetails();
				while(specRs.next()) {
					String specName = specRs.getString("SPEC_NAME");
					String specDetailName = specRs.getString("DESCRIPTION");
					if(!StringUtils.equals(currentSpec.getSpecTitle(), specName)) {
						currentSpec = new Spec();
						currentSpec.setSpecTitle(specName);
						guitar.getSpecs().add(currentSpec);
					}
					if(!StringUtils.equals(currentSpecDetails.getDescription(), specDetailName)) {
						currentSpecDetails = new SpecDetails();
						currentSpecDetails.setDescription(specDetailName);
						currentSpec.getSpecDetails().add(currentSpecDetails);
					}
					int yearProduced = specRs.getInt("YEAR_PRODUCED");
					if(!specRs.wasNull()) {
						currentSpecDetails.getYearsProduced().add(yearProduced);
					}
				}
			}
		}
	}
	
	public void storeGuitar(Guitar guitar) throws IbanezException {
		String insertGuitarSql = "INSERT INTO GUITAR (MODEL_NAME, DESCRIPTION) VALUES (?,?)";
		String insertImageSql = "INSERT INTO GUITAR_IMAGE (GUITAR_ID, IMAGE_PATH) VALUES (?,?)";
		String insertSpecDetailSql = "INSERT INTO GUITAR_SPEC (GUITAR_ID, SPEC_ID, SPEC_DETAIL_ID, YEAR_PRODUCED) VALUES (?,?,?,?)";
		
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
			}
			
			// image
			try(PreparedStatement stmt = conn.prepareStatement(insertImageSql)) {
				for(String imgUrl : guitar.getImageUrls()) {
					String imgFileName = UUID.randomUUID().toString() + "." + getFileExtensionFromUrl(imgUrl);
					try(InputStream in = new URL(imgUrl).openStream()){
						Files.copy(in, Paths.get("C:\\ibanez-images\\" + imgFileName));
					}
					stmt.setInt(1, guitarId);
					stmt.setString(2, imgFileName);
					stmt.executeUpdate();
				}
			}
			
			// spec
			for(Spec spec : guitar.getSpecs()) {
				int specId = getOrInsertSpec(spec.getSpecTitle());
				// spec detail
				try(PreparedStatement stmt = conn.prepareStatement(insertSpecDetailSql)) {
					for(SpecDetails detail : spec.getSpecDetails()) {
						int specDetailId = getOrInsertSpecDetail(detail.getDescription());
						addSpecToSpecDetailMapping(specId, specDetailId);
						List<Integer> yearsProduced = detail.getYearsProduced().isEmpty() ? Arrays.asList((Integer)null) : detail.getYearsProduced();
						for(Integer year : yearsProduced) {
							stmt.setInt(1, guitarId);
							stmt.setInt(2, specId);
							stmt.setInt(3, specDetailId);
							if(year == null) {
								stmt.setNull(4, Types.INTEGER);
							} else {
								stmt.setInt(4, year);
							}
							stmt.executeUpdate();
						}
					}
				}
			}
		} catch (SQLException | IOException | RuntimeException ex) {
			throw new IbanezException("Failed to store guitar " + guitar.getModelName(), ex);
		}
	}
	
	private int getOrInsertSpec(String specName) throws SQLException {
		// get or insert spec
		String selectSql = "SELECT SPEC_ID FROM SPEC WHERE SPEC_NAME = ?";
		String insertSql = "INSERT INTO SPEC (SPEC_NAME) VALUES (?)";
		int specId = -1;
		try(PreparedStatement stmt = conn.prepareStatement(selectSql)) {
			stmt.setString(1, specName);
			try(ResultSet rs = stmt.executeQuery()) {
				while(rs.next()) {
					specId = rs.getInt("SPEC_ID");
				}
			}
		}
		if(specId == -1) {
			try(PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, specName);
				stmt.executeUpdate();
				try(ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()){
						specId = rs.getInt(1);
					}
				}
			}
		}
		
		return specId;
	}
	
	private int getOrInsertSpecDetail(String specDetailDescription) throws SQLException {
		// get or insert spec
		String selectSql = "SELECT SPEC_DETAIL_ID FROM SPEC_DETAIL WHERE DESCRIPTION = ?";
		String insertSql = "INSERT INTO SPEC_DETAIL (DESCRIPTION) VALUES (?)";
		int specDetailId = -1;
		try(PreparedStatement stmt = conn.prepareStatement(selectSql)) {
			stmt.setString(1, specDetailDescription);
			try(ResultSet rs = stmt.executeQuery()) {
				while(rs.next()) {
					specDetailId = rs.getInt("SPEC_DETAIL_ID");
				}
			}
		}
		if(specDetailId == -1) {
			try(PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, specDetailDescription);
				stmt.executeUpdate();
				try(ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()){
						specDetailId = rs.getInt(1);
					}
				}
			}
		}
		
		return specDetailId;
	}
	
	private void addSpecToSpecDetailMapping(int specId, int specDetailId) throws SQLException {
		String insertSql = "INSERT IGNORE INTO SPEC_DETAIL_MAPPING (SPEC_ID, SPEC_DETAIL_ID) VALUES (?, ?)";
		try(PreparedStatement stmt = conn.prepareStatement(insertSql)) {
			stmt.setInt(1, specId);
			stmt.setInt(2, specDetailId);
			stmt.executeUpdate();
		}
	}
	
	private Connection getConnection(String schema) throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + schema, "ibanez", "whatyougonnad0-ohsn4p");
		} catch (ClassNotFoundException ex) {
			throw new SQLException("Failed to load driver class");
		}
	}
	
	private static String getFileExtensionFromUrl(String url) {
		String[] tokens = url.split("\\.");
		return tokens[tokens.length - 1];
	}
	
	@Override
	public void close() throws Exception {
		conn.close();
	}

}
