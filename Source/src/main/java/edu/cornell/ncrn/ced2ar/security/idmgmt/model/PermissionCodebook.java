package edu.cornell.ncrn.ced2ar.security.idmgmt.model;

import java.io.Serializable;
import java.util.List;
/**
 * Model class that holds codesbooks for a given permission.
 * @author Cornell University, Copyright 2012-2015 
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 *
 */

public class PermissionCodebook  implements Serializable{
	private static final long serialVersionUID = 7762174345835573977L;
	
	private String permissionId;
	private List<String> codebookHandle;
	
	public String getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	public List<String> getCodebookHandle() {
		return codebookHandle;
	}
	public void setCodebookHandle(List<String> codebookHandle) {
		this.codebookHandle = codebookHandle;
	}
}
