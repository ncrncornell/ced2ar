package edu.ncrn.cornell.ced2ar.sql.dao;

import java.util.ArrayList;

import edu.ncrn.cornell.ced2ar.sql.models.VariableNote;

public interface VariableNoteDAO {
	public ArrayList<VariableNote> getNotesForVar(String variableName, String codebookID);
	public ArrayList<VariableNote> getNotesForCodebook(String codebookID);
}
