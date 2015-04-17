package edu.ncrn.cornell.ced2ar.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import edu.ncrn.cornell.ced2ar.sql.dao.VariableNoteDAO;
import edu.ncrn.cornell.ced2ar.sql.models.VariableNote;

public class VariableNoteJDBC implements VariableNoteDAO{
	
	private DataSource dataSource;
	 
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/**
	 * Retrieves all values for a variable
	 */
	public ArrayList<VariableNote> getNotesForVar(String variableName, String codebookID) {
		String query = "SELECT * FROM variablenotes WHERE codebookID = ? and variableName = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableNote> notes = new ArrayList<VariableNote>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			statement.setString(2, variableName);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableNote note = new VariableNote();
				note.setName(rs.getString("variableName"));
				note.setCodebookID(rs.getString("codebookID"));
				note.setID(rs.getInt("noteID"));
				note.setValue(rs.getString("noteValue"));
				notes.add(note);
			}
			rs.close();
			statement.close();
			return notes;
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
	 * Retrieves all values for a codebook
	 */
	public ArrayList<VariableNote> getNotesForCodebook(String codebookID) {
		String query = "SELECT * FROM variablenotes WHERE codebookID = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<VariableNote> notes = new ArrayList<VariableNote>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			rs = statement.executeQuery();
			while(rs.next()){
				VariableNote note = new VariableNote();
				note.setName(rs.getString("variableName"));
				note.setCodebookID(rs.getString("codebookID"));
				note.setID(rs.getInt("noteID"));
				note.setValue(rs.getString("noteValue"));
				notes.add(note);
			}
			rs.close();
			statement.close();
			return notes;
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