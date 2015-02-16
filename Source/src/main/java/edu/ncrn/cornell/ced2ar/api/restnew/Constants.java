package edu.ncrn.cornell.ced2ar.api.restnew;

/**
 * This class defines Constants used in the Spring RESTful endpoints 	
 * Part of the new API, WIP	
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */ 
public class Constants {
	private static final String SPRING_RESTFUL_URL_PREFIX= "/restful";
	
	public static final String GET_ALL_CODEBOOKS = SPRING_RESTFUL_URL_PREFIX+ "/codebooks";
	public static final String GET_CODEBOOK = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}";
	public static final String GET_CODEBOOK_ACCESS = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/access";
	public static final String GET_CODEBOOK_FILE_DESC = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/filedesc";
	public static final String GET_CODEBOOK_DOC_DESC = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/docdesc";
	public static final String GET_CODEBOOK_HAS_PDF = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/haspdf";
	public static final String GET_CODEBOOK_RELEASE	= SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/release";
	public static final String GET_CODEBOOK_STUDY_DESC = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/studydesc";
	public static final String GET_CODEBOOK_TITLE_PAGE = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/titlepage";
	public static final String GET_CODEBOOK_VARIABLES = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/variables";
	public static final String GET_CODEBOOK_VERSIONS = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/versions";
	public static final String GET_CODEBOOK_VARIABLE = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/variables/{variableName}";
	public static final String GET_CODEBOOK_VARIABLE_ACCESS = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/variables/{variableName}/access";
	public static final String GET_CODEBOOK_VARIABLE_GROUPS = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/vargroups";
	public static final String GET_CODEBOOK_VARIABLE_GROUP = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/vargroups/{varGrpID}";
	public static final String GET_CODEBOOK_GROUP_VARIABLES = SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/vargroups/{varGrpID}/vars";
	public static final String GET_CODEBOOK_VARIABLE_VERSIONS	= SPRING_RESTFUL_URL_PREFIX+ "/codebooks/{codebookId}/variables/{variableName}/versions";

	public static final String GET_SCHEMA = SPRING_RESTFUL_URL_PREFIX+ "/schemas/{name}";
	public static final String GET_SCHEMA_DOC_TYPE = SPRING_RESTFUL_URL_PREFIX+ "/schemas/{name}/doc/{type}";

	public static final String GET_PROV = SPRING_RESTFUL_URL_PREFIX+ "/prov";
	public static final String GET_WELCOME = SPRING_RESTFUL_URL_PREFIX;
}
