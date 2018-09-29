package edu.cornell.ncrn.ced2ar.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import edu.cornell.ncrn.ced2ar.sql.dao.VariableSumStatDAO;
import edu.cornell.ncrn.ced2ar.sql.models.VariableSumStat;

public class VariableSumStatJDBC implements VariableSumStatDAO{
	
	private DataSource dataSource;
	 
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/**
	 * Retrieves all summary statistics for a variable
	 */
	public ArrayList<VariableSumStat> getStatsForVar(String variableName, String codebookID) {
		String query = "SELECT * FROM variablesumstats WHERE codebookID = ? and variableName = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableSumStat> statList = new ArrayList<VariableSumStat>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			statement.setString(2, variableName);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableSumStat stat = new VariableSumStat();
				stat.setName(rs.getString("variableName"));
				stat.setCodebookID(rs.getString("codebookID"));
				stat.setStat(rs.getString("stat"));
				stat.setStatValue(rs.getDouble("statValue"));
				statList.add(stat);
			}
			rs.close();
			statement.close();
			return statList;
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
	 * Retrieves all summary statistics for a codebook
	 */
	public ArrayList<VariableSumStat> getStatsForCodebook(String codebookID) {
		String query = "SELECT * FROM variablesumstats WHERE codebookID = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableSumStat> statList = new ArrayList<VariableSumStat>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableSumStat stat = new VariableSumStat();
				stat.setName(rs.getString("variableName"));
				stat.setCodebookID(rs.getString("codebookID"));
				stat.setStat(rs.getString("stat"));
				stat.setStatValue(rs.getDouble("statValue"));
				statList.add(stat);
			}
			rs.close();
			statement.close();
			return statList;
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