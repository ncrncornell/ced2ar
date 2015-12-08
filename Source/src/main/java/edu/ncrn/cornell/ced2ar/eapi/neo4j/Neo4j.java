package edu.ncrn.cornell.ced2ar.eapi.neo4j;

import edu.ncrn.cornell.ced2ar.api.data.Config;
import edu.ncrn.cornell.ced2ar.api.data.Connector;
import edu.ncrn.cornell.ced2ar.api.data.Connector.RequestType;

/**
 *Handles interactions with the Neo4j database
 *
 *@author Cornell University, Copyright 2012-2015
 *@author Kyle Brumsted, Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class Neo4j {
	
	private final static String GRAPH_ENDPOINT = Config.getInstance().getNeo4jEndpoint();
	private final static String GRAPH_HASH = Config.getInstance().getNeo4jHash();
	
	/**
	 * Executes cypher statements where a graph is expected in return
	 * @param q the query
	 * @return JSON representing the graph
	 * 
	 * NOTE: the only difference between executeGraph and executeNoGraph is
	 * that the former calls for the results in the form of a graph.
	 * This happens in the query clause "resultDataContents" : ["graph"]
	 * 
	 */
	public static String executeGraphCypher(String q){	
		String uri = GRAPH_ENDPOINT;
		String hash = GRAPH_HASH;
		String response = "";	
		
		Connector c = new Connector(uri);
		try{	
			c.buildRequest(RequestType.POST);
			c.setHeader("Accept", "application/json; charset=UTF-8");
			c.setHeader("Content-Type", "application/json");
			if(!hash.equals("")) c.setHeader("Authorization", "Basic "+hash);
			String body = "{\"statements\" : [ {\"statement\" : \""+q+"\",\"resultDataContents\" : [ \"graph\" ] } ]}";
			c.setPostBody(body);
			response = c.execute();
		}finally{
			c.close();
		}
		return response;
	}
	
	/**
	 * Executes a cypher query where no graph is expected in return
	 * @param q the query
	 * @return JSON with the result
	 */
	public static String executeNoGraphCypher(String q){
		String uri = GRAPH_ENDPOINT;
		String hash = GRAPH_HASH;
		String response = "";		
		Connector c = new Connector(uri);
		try{
			c.buildRequest(RequestType.POST);
			c.setHeader("Accept", "application/json; charset=UTF-8");
			c.setHeader("Content-Type", "application/json");
			if(!hash.equals("")) c.setHeader("Authorization", "Basic "+hash);
			String body = "{\"statements\" : [ {\"statement\" : \""+q+"\"} ]}";
			c.setPostBody(body);
			response = c.execute();
			c.close();
		}finally{
			c.close();
		}
		return response;
	}
	
	/**
	 * Executes a list of cypher queries already properly formatted where a graph is not expected in return
	 * @param q the query
	 * @return JSON with query results
	 */
	public static String executeBatch(String q){
		String uri = GRAPH_ENDPOINT;
		String hash = GRAPH_HASH;
		String response = "";
		Connector c = new Connector(uri);
		try{
			c.buildRequest(RequestType.POST);
			c.setHeader("Accept", "application/json; charset=UTF-8");
			c.setHeader("Content-Type", "application/json");
			if(!hash.equals("")) c.setHeader("Authorization", "Basic "+hash);
			String body = "{\"statements\" : ["+q+"]}";
			c.setPostBody(body);
			response = c.execute();
		}finally{
			c.close();
		}
		return response;
	}	
}