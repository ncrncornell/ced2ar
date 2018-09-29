package edu.cornell.ncrn.ced2ar.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import edu.cornell.ncrn.ced2ar.sql.dao.VariableValueDAO;
import edu.cornell.ncrn.ced2ar.sql.models.VariableValue;

public class VariableValueJDBC implements VariableValueDAO{
	
	private DataSource dataSource;
	 
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/**
	 * Retrieves all values for a variable
	 */
	public ArrayList<VariableValue> getValuesForVar(String variableName, String codebookID) {
		String query = "SELECT * FROM variablevalues WHERE codebookID = ? and variableName = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableValue> valList = new ArrayList<VariableValue>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			statement.setString(2, variableName);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableValue val = new VariableValue();
				val.setName(rs.getString("variableName"));
				val.setCodebookID(rs.getString("codebookID"));
				val.setValue(rs.getString("value"));
				val.setValueLabel(rs.getString("valueLabel"));
				valList.add(val);
			}
			rs.close();
			statement.close();
			return valList;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}finally{		
			try{
				if(conn != null) conn.close();
			}catch(SQLException e){}
			
			try{
				if(statement != null) statement.close();
			}catch(SQLException e){}
			
			try{
				if(rs != null) rs.close();
			}catch(SQLException e){}	
		}
	}
	/**
	 * Retrieves all values with a codebook
	 */
	public ArrayList<VariableValue> getValuesForCodebook(String codebookID) {
		String query = "SELECT * FROM variablevalues WHERE codebookID = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableValue> valList = new ArrayList<VariableValue>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableValue val = new VariableValue();
				val.setName(rs.getString("variableName"));
				val.setCodebookID(rs.getString("codebookID"));
				val.setValue(rs.getString("value"));
				val.setValueLabel(rs.getString("valueLabel"));
				valList.add(val);
			}
			rs.close();
			statement.close();
			return valList;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}finally{		
			try{
				if(conn != null) conn.close();
			}catch(SQLException e){}
			
			try{
				if(statement != null) statement.close();
			}catch(SQLException e){}
			
			try{
				if(rs != null) rs.close();
			}catch(SQLException e){}	
		}
	}
}