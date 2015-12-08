package edu.ncrn.cornell.ced2ar.sql.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import edu.ncrn.cornell.ced2ar.sql.dao.VariableDAO;
import edu.ncrn.cornell.ced2ar.sql.models.Variable;

public class VariableJDBC implements VariableDAO{
	
	private DataSource dataSource;
	 
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/**
	 *Adds variable to database
	 */
	public void insert(Variable varaible){
		String sql = "INSERT INTO Variables (name, codebookID, label, originalName, startPos, endPos, type) "
		+"VALUES (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
 
		try{
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, varaible.getName());
			ps.setString(2, varaible.getCodebookID());
			ps.setString(3, varaible.getLabel());
			ps.setString(4, varaible.getOriginalName());
			ps.setInt(5, varaible.getStartPos());
			ps.setInt(6, varaible.getEndPos());
			ps.setString(7, varaible.getType());
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e){
			throw new RuntimeException(e);
 
		}finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}	
	}
	
	/**
	 * Retrieves a variable in a codebook
	 */
	public Variable get(String name, String codebookID){
		String query = "SELECT * FROM variables WHERE name = ? AND codebookID = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, codebookID);
			
			Variable v = null;
			rs = statement.executeQuery();
			if(rs.next()) {
				v = new Variable();
				v.setName(rs.getString("name"));
				v.setCodebookID(rs.getString("name"));
				v.setlabel(rs.getString("name"));
				v.setOriginalName(rs.getString("name"));
				v.setStartPos(rs.getInt("startPos"));
				v.setEndPos(rs.getInt("endPos"));
				v.setType(rs.getString("type"));
			}
			rs.close();
			statement.close();
			return v;
		}catch(SQLException e){
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
	 * Retrieves all variables in a codebook
	 */
	public ArrayList<Variable> getVarsInCodebook(String codebookID) {
		String query = "SELECT * FROM variables WHERE codebookID = ?";
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<Variable> varList = new ArrayList<Variable>();
		try{
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(query);
			statement.setString(1, codebookID);
			rs = statement.executeQuery();
			while(rs.next()){
				Variable v = new Variable();
				v.setName(rs.getString("name"));
				v.setCodebookID(rs.getString("codebookID"));
				v.setlabel(rs.getString("label"));
				v.setOriginalName(rs.getString("originalName"));
				v.setStartPos(rs.getInt("startPos"));
				v.setEndPos(rs.getInt("endPos"));
				v.setType(rs.getString("type"));
				varList.add(v);
			}
			rs.close();
			statement.close();
			return varList;
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