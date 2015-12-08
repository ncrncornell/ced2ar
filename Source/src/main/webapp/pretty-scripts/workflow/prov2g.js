var s;
var zoomMin = 0.1;
var zoomMax = 1.5;
var graphBuilt = false;
var showingInfo = false; 
var selectedNode = null;

var showingAuthors = true;
var showingTexts = true;
var showingTheses = true;

var nodeColor = "rgba(42,100,150,.9)";
var nodeColorSelected = "rgba(202,70,66,.9)";
var nodeColorDiscovered = "rgba(25,60,90,.9)";
var nodeColorDeselect = "rgba(42,100,150,.25)";

var edgeColor = "rgba(200,200,200,.9)";
var edgeDeselectColor = "rgba(200,200,200,.25)";
var edgeInColor = "rgba(42,100,150,.75)";
var edgeOutColor = "rgba(202,70,66,.5)";

//Initial building
faConfig = {
	barnesHutOptimize: true,	
	barnesHutTheta:0.75,
	linLogMode: false,
	adjustSizes: false,
	edgeWeightInfluence: 20,
	scalingRatio:4,
	strongGravityMode: true,
	gravity: 1,
	slowDown: 1000	            		 
}; 

//No movement
faConfig2 = {
	linLogMode: false,
	adjustSizes: true,
	edgeWeightInfluence: 10,
	scalingRatio:1,
	strongGravityMode: false,
	gravity: 0,
	slowDown: 100000	                		 
};  

//Manual
faConfig3 = {
	barnesHutOptimize: true,	
	barnesHutTheta:0.5,
	linLogMode: true,
	adjustSizes: true,
	edgeWeightInfluence: 10,
	scalingRatio:1,
	strongGravityMode: true,
	gravity: 1,
	slowDown: 50                 		 
};  


$(document).ready(function(){	
	baseURI = $('#meta_uri').html();
	buildGraph();
	redraw();
	filterListen();
});

function buildGraph(){
	graphBuilt = false;
	var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0) * 0.7;	
	if(document.documentElement.clientHeight < 900){
		h = h* 0.85;
	}
	
	$("#graph2").css("height",h+"px");
	$("#graph2").css("cursor","move");
	$("#provShuffle").addClass("disabled");	

	var g = {
        nodes: [],
        edges: []
    };

	s = new sigma({
	    graph: g,
	    container: 'graph2',
	    renderer: {
	        container: document.getElementById('graph2'),
	        type: 'canvas'
	    },
	    settings:{
	    	doubleClickEnabled:false,
	        sideMargin:2,
	        zoomMin:zoomMin,
	        zoomMax:zoomMax,
	        minNodeSize: 8*(h/475),
	        maxNodeSize: 24*(h/450),

	        defaultLabelColor:"#000",
	        defaultLabelHoverColor:"#eee",
	        defaultHoverLabelBGColor:"#333",
	        defaultNodeBorderColor:"rgba(0,0,0,.9)",
	        
	        defaultEdgeLabelColor:"#000",
	        defaultEdgeLabelActiveColor: '#000',
	        defaultEdgeLabelSize: $("#edgeLabelToggle").prop('checked') ? 6 : 0,
	        edgeLabelSizePowRatio: 2,
	        edgeLabelThreshold: 3,
	        edgeLabelSize:'proportional',
	        labelSizeRatio:"1.25",
	        labelThreshold:8,
	        
	        /*labelSize:'proportional',*/
	        minEdgeSize:1,
	        maxEdgeSize:5
	    }
	});
	var startingNode = $("#startingNode").val();
	drawGraph(startingNode,faConfig,2,true);
}

/**
 * Draws nodes from a root
 * @param s - sigma js object
 * @param root - id of root node to start from
 * @param config - force atlas config settings
 * @param depth - depth to travel beyond root
 * @param listeners - after drawing, whether or not to load listeners
 */
function drawGraph(root,config,depth,listeners){
	var totalNodes = 0;
	//TODO: attr to prevent traversal already clicked
	//TODO: hide out going
	$.ajax({
        cache: false,
        async: false,
        type: "GET",
        url: baseURI+"/prov/data3?start="+root+"&depth="+depth,
        complete: function(r){
        	if(r.status < 400){
        		var data = JSON.parse(r.responseText).results[0].data;       
        		for(var i in data){
        			var rootObj;
        			if(graphBuilt){	
        				rootObj = s.graph.nodes(root);
        			}
        			var nodes = data[i].graph.nodes;
        			var edges = data[i].graph.relationships;		
        			for(var k in nodes){	
        				var x = 0.0;
                		var y = 0.0;
        				if(graphBuilt){	
                			var size = rootObj.size;	
                			var xMin = rootObj.x + size*-0.125;
                			var xMax = rootObj.x + size*0.125;
                			var yMin = rootObj.y + size*-0.125;
                			var yMax = rootObj.y + size*0.125;
                			x = Math.random() * (xMax - xMin) + xMin;
                			y = Math.random() * (yMax - yMin) + yMin;
                		}else{
                			x = Math.random();
                			y = Math.random();
                		}
        				totalNodes += addNode(nodes[k],root,x,y);
        			}
        			for(var l in edges){
        				addEdge(edges[l]);
        			}
        		}
        	}
        	if(totalNodes === 0){
        		config = faConfig2;   		
        	}
        } 
    });
	
	

	//Resizes nodes based off out degree
	var nodes = s.graph.nodes();
	for(var i in nodes){
		var node = nodes[i];
		switch(node.nodeType){
		case "Provider":
			node.size = s.graph.degree(node.id,"in")*0.75;
			break;
		default:
			node.size = s.graph.degree(node.id,"out")*0.5 + s.graph.degree(node.id,"in")*0.5;
			break;
		}
		
	}
	
	//Resizes edges based off target in degree
	var edges = s.graph.edges();
	for(var k in edges){
		var edge = edges[k];
		var indegreeTarget = s.graph.degree(edge.target,"in");		
		var size = indegreeTarget/2;
		var color = "rgba(200,200,200,"+indegreeTarget/5+")";
		edge.size = size;
		edge.color = color;
	}
	
	s.graph.nodes(root).expanded = true;
	s.refresh();
	s.configForceAtlas2(config);
    s.startForceAtlas2();	

    //Set graph to stop scaling after 750ms
	//Inner code is ran after graph is built
    setTimeout(function(){ 
    	graphBuilt = true;
    	s.stopForceAtlas2(); 
    	s.killForceAtlas2();
    	$("#provShuffle").removeClass("disabled");	
    	toggleEdgeLabels();
    	
    	if(listeners){
	    	//Change cursor on hovering node
	    	s.bind('overNode outNode', function(e){
	    		if(e.type === "overNode"){
	    			$("#graph2").css("cursor","pointer");
	    		}else{
	    			$("#graph2").css("cursor","move");
	    		}
	    	});
	    	
	    	s.bind('clickNode', function(e){
	    		select(e);	
	        });
	    	
	    	//Rotation Controls
        	var rotating = false;
        	var keyRotateCCW;
        	$("#provRotateCCW").unbind();
        	$("#provRotateCCW").mousedown(function(){
        	    if(!rotating){    	
        	    	rotating = true;
        	    	keyRotateCCW = setInterval(function() {
        	    		rotate(0.01);
    				}, 5);
        	    }
        	}).mouseup(function() {
    				clearInterval(keyRotateCCW);
    				keyRotateCCW = null;
    				rotating = false;
        	}).mouseout(function() {
				clearInterval(keyRotateCCW);
				keyRotateCCW = null;
				rotating = false;
        	});
        	
        	var keyRotateCW;
        	$("#provRotateCW").unbind();
        	$("#provRotateCW").mousedown(function(){
        	    if(!rotating){   
        	    	rotating = true;
        	    	keyRotateCW = setInterval(function() {
        	    		rotate(-0.01);
    				}, 5);
        	    }
        	}).mouseup(function() {
    				clearInterval(keyRotateCW);
    				keyRotateCW = null;
    				rotating = false;
        	}).mouseout(function() {
				clearInterval(keyRotateCW);
				keyRotateCW = null;
				rotating = false;
        	});    	
	    	
	    	//Clusters graph when button is held
	    	var clustering = false;
	    	$("#provCluster").mousedown(function(){
	    	    if(!clustering){    	
	    	    	s.configForceAtlas2(faConfig3);
	            	s.startForceAtlas2();
	    	    }
	    	}).mouseup(function() {
	    		s.stopForceAtlas2(); 
	         	s.killForceAtlas2();
				clustering = false;
	    	}).mouseout(function() {
	    		s.stopForceAtlas2(); 
	         	s.killForceAtlas2();
				clustering = false;
	    	});
	    	
	    	
    	}
    }, 750);  
}

/**
 * Rotates the graph
 * @param sigma  s - graph instance
 * @param double delta - amount to rotate
 */
function rotate(delta){
	
	var cA = s.camera.angle;
	var cR = s.camera.ratio;
	var cX = s.camera.x;
	var cY = s.camera.y;
	var moveRatio = 100;
	
	if(cY > 1){
		cY -= cY/moveRatio;
	}else if(cY < -1){
		cY -= cY/moveRatio;
	}else{
		cY = 0;
	}
	
	if(cX > 1){
		cX -= cX/moveRatio;
	}else if(cX < -1){
		cX -= cX/moveRatio;
	}else{
		cX = 0;
	}
		
	s.camera.goTo({
      x: cX,
      y: cY,
      angle: cA + delta,
      ratio: cR
    });
}

/**
 * Adds a node to the graph
 * @param s
 * @param node
 */
function addNode(node,root,x,y){
	var id = node.id;
	if(s.graph.nodes(id) == null){
		var name = node.properties.displayName;
		var author = node.properties.author;
		var size = 1;
		var label = name;
		var expanded = id == root ? true : false;
		var color = expanded ? nodeColorDiscovered : nodeColor; 
		var hidden = false;
		var nodeType = node.labels["0"];
		var shape = "square";
		var handle = "";
		
		switch(nodeType){
			case "Provider":
				shape = "circle";
				hidden = !showingAuthors;	
			break;
			case "Dataset":
				handle = node.properties.handle;
				shape = "square";
				hidden = !showingTexts;	
			break;
			case "Program":
				shape = "diamond";
				hidden = !showingTheses;		
			break;
		}
		
		s.graph.addNode({
	      id: id,
	      name:name,
	      handle:handle,
	      author:author,
	      label: label,
	      nodeType: nodeType,
	      expanded: expanded,
	      x: x,
	      y: y,
	      size: size,
	      color:color,
	      hidden:hidden,
	      type:shape
	    });
		return 1;
	}
	return 0;
}

/**
 * Adds an edge to the graph
 * @param s
 * @param edge
 */
function addEdge(edge){
	var id = edge.id;
	if(s.graph.edges(id) == null){
		var source = edge.startNode;
		var target = edge.endNode;
		var edgeType = edge.type.replace("_"," ").toLowerCase();
		s.graph.addEdge({
	        id: id,
	        source: source,
	        target: target,
	        edgeType: edgeType,
	        label: edgeType,
	        color: edgeColor,
			type: "arrow",
			size: 2
	    });
	}
}

/**
 * Event for selecting nodeee
 * @param s
 * @param e
 */
function select(e){
	var id = e.data.node.id;	
	toggleInfoBox(e);
}

function toggleEdgeLabels(){
	$("#edgeLabelToggle").change(function(){	
		edgeLabels();
	});	
}

function edgeLabels(){
	if($("#edgeLabelToggle").prop('checked')){
		s.settings('defaultEdgeLabelSize','6');
	}else{
		s.settings('defaultEdgeLabelSize','0');
	}
	draw(faConfig2,10);
	return;
}

/**
 * Draws custom Really Fancy© info box
 * @param s
 * @param node
 */
//TODO: Highlight incoming and outgoing edges on select?
//TODO: Limit width of box - some long homepage urls make it too big

function toggleInfoBox(e){
	showingInfo = !showingInfo;
	//If node is selected, and user clicks on a different node
	if(selectedNode != e.data.node.id && !showingInfo){
		showingInfo = true;	
		s.graph.nodes(selectedNode).color = s.graph.nodes(selectedNode).expanded ? nodeColorDiscovered : nodeColor;
	}
	
	if(showingInfo){
		//Select node and draw box
		selectedNode = e.data.node.id;
		e.data.node.color = nodeColorSelected;
		recolor(selectedNode);
		
		var info = "Name: "+ e.data.node.name;
		//info += "<br /> Graph ID: "+ e.data.node.id;
		info += "<br /> Type : "+ e.data.node.nodeType;
		switch(e.data.node.nodeType){
			case "Dataset":
				if(e.data.node.handle != null){
					var h = e.data.node.handle.split(".");
					info += "<br />Metadata: <a href='"+baseURI+"/codebooks/"+h[0]+"/v/"+h[1]+"'>"+e.data.node.handle+"</a>";
				}else{
					info += "<br />Metadata: None specified";
				}				
				break;
			case "Program":
				info += "<br /> Author : "+ e.data.node.author;
				break;
			case "Provider":
				break;
		}
		info += "<br /><a href='"+baseURI+"/edit/workflow/n/"+e.data.node.id+"'>View/Edit Details</a>";
		
		if(!e.data.node.expanded){
			info += "<br /><button class='nodeExpand btn' title='Show more nodes' value='"+e.data.node.id+"'>";
			info += "<i class='fa fa-plus'></i> Expand </button>";		
		}else{
			info += "<br /><em>All edges found</em>";
		}

		info = "<p>"+info+"</p>";
		
		$("#graph2Info").html(info);
		$("#graph2Info").css("padding",".5em 1em");	
		
	}else{
		//Deselect node and hide box
		clearRecolor();
		e.data.node.color = e.data.node.expanded ? nodeColorDiscovered : nodeColor;
		selectedNode = null;
		$("#graph2Info").empty();
		$("#graph2Info").css("padding","0em");
	}
	draw(faConfig2,100);
	expandFromNode();
}

function recolor(id){	
	var selectedNodes = [];
	selectedNodes.push(id);
	
	var edges = s.graph.edges();
	for(var i in edges){
		var edge = edges[i];
		edge.defaultEdgeLabelSize = 0;
		if(edge.source == id){
			selectedNodes.push(edge.target);
			edge.color = edgeOutColor;
			if(edge.label === "") edge.label = edge.hideLabel;
		}else if(edge.target == id){
			selectedNodes.push(edge.source);
			edge.color = edgeInColor;
			if(edge.label === "") edge.label = edge.hideLabel;
		}else{
			edge.hideLabel = edge.label;
			edge.label = "";
			edge.color = edgeDeselectColor;
		}
	}
	
	var nodes = s.graph.nodes();
	for(var k in nodes){
		var node = nodes[k];
		if(node.id != id){
			if(selectedNodes.indexOf(node.id) == -1){
				node.color = nodeColorDeselect;
			}else{
				node.color = nodeColor;
			}
		}
	}	
}

/**
* Removes highlight when deselecting
*/
function clearRecolor(){
	var edges = s.graph.edges();
	for(var i in edges){
		var edge = edges[i];
		edge.color = edgeColor;
		if(edge.label === "") edge.label = edge.hideLabel;
	}
	
	var nodes = s.graph.nodes();
	for(var k in nodes){		
		var node = nodes[k];
		node.color = nodeColor;
	}
}

/**
 * Listens for expand button to be click
 */
//TODO: Return message if already expanded or no new nodes?
function expandFromNode(){
	$("#graph2Info .nodeExpand").click(function (){ 
		var id = $(this).val();
		drawGraph(id,faConfig,1,false);				
		$("#graph2Info p").append("<em>All edges found</em>");
		$("#graph2Info .nodeExpand").remove();		
	});
}

/**
 *Draws the graph
 */
function draw(config,timeout){
 	s.configForceAtlas2(config);
	s.startForceAtlas2();
	setTimeout(function() { 
     	s.stopForceAtlas2(); 
     	s.killForceAtlas2();
    }, timeout);	
}

/**
 *Listen for a redraw button click
 */
function redraw(){
	$("#provShuffle").click(function(){
		redrawGraph();
	});
}
/**
 * Redraws the graph
 */
function redrawGraph(){
	selectedNode = null;
	showingInfo = false; 
	$("#graph2").empty();
	$("#graph2Info").empty();
	$("#graph2Info").css("padding","0em");
	buildGraph();	
}

/**
 *Listens for changes in the filter
 */
function filterListen(){
	$("form[name=provFilter]").change(function (){	
		showingAuthors = $("input[name=authors]",this).is(':checked');
		showingTexts = $("input[name=texts]",this).is(':checked');
		showingTheses = $("input[name=theses]",this).is(':checked');
		filter();
	});
}

/**
 * Filters nodes by type
 * @param authors
 * @param texts
 * @param theses
 */
function filter(authors,texts,theses){
	if(!graphBuilt){
		return;
	}
	
	var nodes = s.graph.nodes();
	for(var i in nodes){	
		var node = nodes[i];
		var type = node.nodeType.toLowerCase();		
		switch(type){
			case "author":
				node.hidden = !showingAuthors;				
			break;
			case "text":
				node.hidden = !showingTexts;				
			break;
			case "thesis":
				node.hidden = !showingTheses;				
			break;
		}		
	}
	draw(faConfig2,10);
	return;
}