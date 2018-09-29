package edu.cornell.ncrn.ced2ar.sql.dao;

import java.util.ArrayList;

import edu.cornell.ncrn.ced2ar.sql.models.Variable;

public interface VariableDAO {
	public void insert(Variable varaibles);
	public Variable get(String name, String codebookID);
	public ArrayList<Variable> getVarsInCodebook(String codebookID);
}
