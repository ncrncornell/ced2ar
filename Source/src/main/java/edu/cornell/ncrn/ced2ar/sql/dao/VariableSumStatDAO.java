package edu.cornell.ncrn.ced2ar.sql.dao;

import java.util.ArrayList;

import edu.cornell.ncrn.ced2ar.sql.models.VariableSumStat;

public interface VariableSumStatDAO {
	public ArrayList<VariableSumStat> getStatsForVar(String variableName, String codebookID);
	public ArrayList<VariableSumStat> getStatsForCodebook(String codebookID);
}
