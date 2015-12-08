package edu.ncrn.cornell.ced2ar.eapi.neo4j;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class containing prepackaged queries and other Neo4j Utility methods
 * 
 * @author Kyle Brumsted, Ben Perry
 */
public class Neo4jUtil {
	
	private static final Logger logger = Logger.getLogger(Neo4jUtil.class);
		
	/**
	 * Retrieves a graph centered around the specified node
	 */
	public static String getLocalSet(String type, String key, String value)
	{
		return Neo4j.executeGraphCypher("MATCH (n:"+type+" {"+key+" : '"+value+"'}) MATCH (n)<-[r*1..2]->() RETURN r LIMIT 40");
	}
	
//RePEC related	
	/**
	 * Searches by author name
	 * @param term
	 * @param limit
	 * @return
	 */
	public static String searchAuthors(String term, int limit){
		String cypher = "MATCH (n:Author) WHERE"
		+ " n.name =~ '(?i).*"+term+".*'"
		+ " RETURN id(n),n.name"
		+ " LIMIT "+limit;
		return Neo4j.executeNoGraphCypher(cypher);
	}

	/**
	 * Selects a node with all outgoing connected nodes
	 * @param id
	 * @return
	 */
	public static String selectNode(int id, int depth){
		if (depth <= 0) depth = 1;
		String cypher = "MATCH (n)-->(o) WHERE id(n)="+id+" WITH n LIMIT 1 MATCH (n)<-[r*1.."+depth+"]->() RETURN r";
		return Neo4j.executeGraphCypher(cypher);
	}
	
	/**
	 * Selects a node with all connected nodes, incoming or outgoing
	 * @param id
	 * @param depth
	 * @return
	 */
	public static String selectNode2(int id, int depth){
		if (depth <= 0) depth = 1;
		String cypher = "MATCH (n)-[r*0.."+depth+"]-() WHERE id(n)="+id+" "
		+ " RETURN r";
		String results = Neo4j.executeGraphCypher(cypher);
		return results;
	}	
	
	/**
	 * Selects a node with all connected nodes, incoming or outgoing, and returns a compact response
	 * @param id
	 * @param depth
	 * @return
	 */
	//TODO: Write a method that retrieve as little info as possible, and avoid duplicates
	//http://localhost:7474/db/data/node/11970/properties
	//http://localhost:7474/db/data/node/11970/relationships/all/
	//TODO:Handle depth
	public static String selectNode4(int id, int depth){
		if (depth <= 0) depth = 1;
		String cypher = "MATCH (n)-[r]-() WHERE id(n)="+id
		+ " RETURN id(startnode(r)),startnode(r).name,id(endnode(r)),endnode(r).name,type(r)";
		//+ " RETURN id(startnode(r)),startnode(r),id(endnode(r)),endnode(r),type(r)";
		String results = Neo4j.executeNoGraphCypher(cypher);
		return results;
	}	
	
//New (for workflow specific applications) TODO:Test and document	
	
	/**
	 * Searches for all workflow related nodes
	 * @param term
	 * @param limit
	 * @return
	 */
	public static String workflowSearch(String term, int limit){
		int l = limit > 3 ? limit/3 : 3;

		String cypher = "MATCH (n:Dataset) WHERE n.displayName =~ '(?i).*"+term+".*' RETURN id(n),n.displayName,labels(n)[0] LIMIT "+l			
			+" UNION MATCH (n:Program) WHERE n.displayName =~ '(?i).*"+term+".*' RETURN id(n),n.displayName,labels(n)[0] LIMIT "+l
			+" UNION MATCH (n:Provider) WHERE n.displayName =~ '(?i).*"+term+".*' RETURN id(n),n.displayName,labels(n)[0] LIMIT "+l;
		
		return Neo4j.executeNoGraphCypher(cypher);
	}
	
	/**
	 * Searches for nodes 
	 * @param term
	 * @param limit
	 * @param type
	 * @return
	 */
	public static String workflowSearch(String term, int limit, String type){
		String cypher = "MATCH (n:"+type+") WHERE n.displayName =~ '(?i).*"+term+".*'"
		+" RETURN id(n),n.displayName,labels(n)[0] LIMIT "+limit;
		return Neo4j.executeNoGraphCypher(cypher);
	}
	
	/**
	 * Retrieves a node, with all incoming and outgoing edges
	 * @param id
	 * @return
	 */
	public static String selectNode3(int id){
		String cypher = "match(n)-[r]->(m) where id(n)="+id+""
		+ " OR id(m)="+id+" return id(n),n.displayName,id(r),type(r),id(m),m.displayName";
		String results = Neo4j.executeNoGraphCypher(cypher);
		return results;
	}
	
	/**
	 * Retrieve a node by id, and first label
	 * @param id
	 * @return
	 */
	public static String fetchNode(int id){
		String cypher = "MATCH (n) WHERE id(n) = "+id+" return n, labels(n)[0]";	
		String results = Neo4j.executeNoGraphCypher(cypher);		
		return results;
	}

	/**
	 * Retrieves all nodes of a type
	 * @param type
	 * @param limit
	 * @return
	 */
	public static String fetchNodes(String type, int limit){
		return fetchNodes(type,limit,0);
	}
	
	/**
	 * Retrieves all nodes of a type
	 * @param type
	 * @param limit
	 * @param skip - number of records to skip
	 * @return
	 */
	public static String fetchNodes(String type, int limit, int skip){
		String cypher = "MATCH (n:"+type+") RETURN id(n),n.name ORDER BY n.name SKIP "+ skip;	
		if(limit > 0) cypher += " LIMIT " + limit;
		String results = Neo4j.executeNoGraphCypher(cypher);		
		return results;
	}
	
	/**
	 * Sets a property for the matching node
	 * @param type
	 * @param name
	 * @param field
	 * @param value
	 */
	public static void setProperty(String type, String name, String field, String value){
		//No spaces allowed for field name TODO: throw error?
		field = field.replace(" ", "_");
		String cypher = "MATCH (n:"+type+" {name:'"+name+"'})"
			+" SET n."+field+" = '"+value+"' RETURN n";
		Neo4j.executeNoGraphCypher(cypher);
	}	
	
	/**
	 * Sets a property for the matching node
	 * @param type
	 * @param name
	 * @param field
	 * @param value
	 */
	public static void setProperty(String id, String field, String value){
		//No spaces allowed for field name TODO: throw error?
		field = field.replace(" ", "_");
		String cypher = "MATCH (n) WHERE id(n) = "+id
			+" SET n."+field+" = '"+value+"' RETURN n";
		Neo4j.executeNoGraphCypher(cypher);
	}
	
	/**
	 * Retrieves number of nodes that match a type and name 
	 * @param type - type of node
	 * @param name - name of node
	 * @return
	 */
	public static int nodeCount(String type, String name){
		String cypher = "MATCH (n:"+type+" {name:'"+name+"'}) return count(n)";
		String result = Neo4j.executeNoGraphCypher(cypher);
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject results = (JSONObject)((JSONArray) obj.get("results")).get(0);
			JSONArray data = (JSONArray) results.get("data");
			JSONObject row = (JSONObject) data.get(0);
			String element = row.getString("row");
			int count = Integer.parseInt(element.substring(1,element.length()-1));
			return count;
		} catch (JSONException e) {
			//Will throw not found exception
			//e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Delets a node, matched by name and type
	 * @param type
	 * @param name
	 */
	//TODO: Find better way to delete node, and possible edges
	public static void deleteNode(String type, String name){
		String cypher = "MATCH (n:"+type+" {name:'"+name+"'})-[r]-() DELETE n,r;";
		String cypher2 = " MATCH (n:"+type+" {name:'"+name+"'}) DELETE n";
		Neo4j.executeNoGraphCypher(cypher);
		Neo4j.executeNoGraphCypher(cypher2);
	}
	
	/**
	 * Delets a node, matched by id
	 * @param type
	 * @param name
	 */
	//TODO: Find better way to delete node, and possible edges
	public static void deleteNode(String id){
		String cypher = "MATCH (n)-[r]-() WHERE id(n) = "+id+" DELETE n,r;";
		String cypher2 = " MATCH (n) WHERE id(n) = "+id+"  DELETE n";
		Neo4j.executeNoGraphCypher(cypher);
		Neo4j.executeNoGraphCypher(cypher2);
	}
	
	/**
	 * Adds a new program node
	 * @param name
	 * @param displayName
	 * @param uri
	 * @param author
	 * @param notes
	 * @return
	 */
	public static String insertProgram(String name, String displayName, String uri, String author, String notes){
		String cypher = "CREATE (n:Program {name :'"+name+"',"
		+ "displayName:'"+displayName+"', uri:'"+uri+"', author:'"+author+"', notes:'"+notes+"'}) return id(n)";
		String r = Neo4j.executeNoGraphCypher(cypher);	
		return r;
	}
	
	/**
	 * Adds a new dataset node
	 * @param name
	 * @param displayName
	 * @param uri
	 * @param notes
	 * @return
	 */
	public static String insertDataset(String name, String displayName, String uri, String notes){
		return insertDataset(name, displayName, uri, "", "", notes);
	}
	
	/**
	 * Adds a new dataset node
	 * @param name
	 * @param displayName
	 * @param uri
	 * @param doi
	 * @param handle
	 * @param notes
	 * @return
	 */
	public static String insertDataset(String name, String displayName, String uri, String doi, 
	String handle, String notes){
		String cypher = "CREATE (n:Dataset {name :'"+name+"',"
		+ "uri:'"+uri+"', doi:'"+doi+"', handle:'"+handle+"', notes:'"+notes+"'}) return id(n)";
		String r = Neo4j.executeNoGraphCypher(cypher);	
		return r;
	}
	
	/**
	 * Adds a new provider
	 * @param name
	 * @param uri
	 * @return
	 */
	public static String insertProvider(String name, String uri){
		String cypher = "CREATE (n:Provider {name :'"+name+"', displayName :'"+name+"',uri:'"+uri+"'}) return id(n)";
		String r = Neo4j.executeNoGraphCypher(cypher);	
		return r;
	}
	
//Edges
	/**
	 * Adds a provides edge from a provider to a dataset
	 * @param provider
	 * @param input
	 */
	public static void insertEdgeProvides(String provider, String input){
		String cypher = "MATCH (p:Provider),(i:Dataset)"
			+" WHERE p.name = '"+provider+"' AND i.name = '"+input+"'"
			+" CREATE (p)-[r:Provides]->(i) RETURN r";	
		Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Adds a provides edge from a provider to a dataset
	 * @param provider
	 * @param input
	 * @return
	 */
	public static String insertEdgeProvides(int provider, int input){
		String cypher = "MATCH (p:Provider),(i:Dataset)"
			+" WHERE id(p) = "+provider+" AND id(i) = "+input+""
			+" CREATE (p)-[r:Provides]->(i) RETURN r";	
		return Neo4j.executeNoGraphCypher(cypher);	
	}

	/**
	 * Adds a used by edge from a dataset to a program
	 * @param input
	 * @param program
	 */
	public static void insertEdgeUsedBy(String input, String program){
		String cypher = "MATCH (i:Dataset),(p:Program)"
			+" WHERE i.name = '"+input+"' AND p.name = '"+program+"'"
			+" CREATE (i)-[r:Used_by]->(p) RETURN r";		
		Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Adds a used by edge from a dataset to a program
	 * @param input
	 * @param program
	 */
	public static String insertEdgeUsedBy(int input, int program){
		String cypher = "MATCH (i:Dataset),(p:Program)"
			+" WHERE id(i) = "+input+" AND id(p) = "+program+""
			+" CREATE (i)-[r:Used_by]->(p) RETURN r";		
		return Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Adds a produced edge from a program to an output
	 * @param program
	 * @param output
	 */
	public static void insertEdgeProduced(String program, String output){
		String cypher = "MATCH (p:Program),(o:Dataset)"
			+" WHERE p.name = '"+program+"' AND o.name = '"+output+"'"
			+" CREATE (p)-[r:Produced]->(o) RETURN r";	
		Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Adds a produced edge from a program to an output
	 * @param program
	 * @param output
	 */
	public static String insertEdgeProduced(int program, int output){
		String cypher = "MATCH (p:Program),(o:Dataset)"
			+" WHERE id(p) = "+program+" AND id(o) = "+output+""
			+" CREATE (p)-[r:Produced]->(o) RETURN id(r)";
		return Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Removes an edge by ID
	 * @param id
	 */
	public static void deleteEdge(int id){
		String cypher = "MATCH (n)-[r]-() WHERE id(r) = "+id+" DELETE r;";
		Neo4j.executeNoGraphCypher(cypher);	
	}
	
	/**
	 * Given ids of the source and the target, is an edge with the primary label of type, a duplicate
	 * @param source
	 * @param target
	 * @param type
	 * @return
	 */
	public static boolean isEdgeDuplicate(int source, int target, String type){
		String cypher = "MATCH (s)-[r:"+type+"]-(t) WHERE id(s) = "+source
		+" AND id(t) = "+target+" RETURN count(r)";
		String result = Neo4j.executeNoGraphCypher(cypher);		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject results = (JSONObject)((JSONArray) obj.get("results")).get(0);
			JSONArray data = (JSONArray) results.get("data");
			JSONObject row = (JSONObject) data.get(0);
			String element = row.getString("row");
			int count = Integer.parseInt(element.substring(1,element.length()-1));
			return count != 0;
		} catch (JSONException e) {}		
		return false;
	}
}