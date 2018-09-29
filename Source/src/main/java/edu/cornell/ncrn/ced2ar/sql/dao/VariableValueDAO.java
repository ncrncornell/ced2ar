package edu.cornell.ncrn.ced2ar.sql.dao;

import java.util.ArrayList;

import edu.cornell.ncrn.ced2ar.sql.models.VariableValue;

public interface VariableValueDAO {
	public ArrayList<VariableValue> getValuesForVar(String variableName, String codebookID);
	public ArrayList<VariableValue> getValuesForCodebook(String codebookID);
}
