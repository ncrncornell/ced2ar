var viewingSubset = false;
var selectedNode = null;
var startingRoot;

var zoomMin = 0.1;
var zoomMax = 1.5;

var selectColor = "#ca4642";//#4282ca//ca4642
var nodeColor = "#2a6496";
var nodeColorLeaf = "rgba(42,100,150,0.5)";
var nodeNullURIColor = "#333"; 
var nodeDisconnectColor = "rgba(42,100,150,0.25)";
	
var edgeColor = "rgba(155,155,155,0.75)";
var outColor = "#d91616";
var inColor = "#00d093";
var srColor = "#d97816";

$(document).ready(function(){	
	startingRoot = $("#startingNode").val();
	$("#graph2").html("<h2>Loading...</h2>");
	graphBuilt = false;
	lastProvS = "";
	baseURI = $('#meta_uri').html();
	buildGraph(startingRoot);
	//$("#provRootNode").chosen();
	shuffle();
});


function buildGraph(root){
	//Resize graph
	var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0) * 0.7;	
	if(document.documentElement.clientHeight < 900){
		h = h* 0.85;
	}
	
	$("#graph2").css("height",h+"px");
	$("#graph2").css("cursor","move");
	
	var preSelect = false;
	$("#provShuffle").addClass("disabled");	
	var g = {
        nodes: [],
        edges: []
    };

	var s = new sigma({
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
	        minNodeSize: 4*(h/475),
	        maxNodeSize: 14*(h/450),

	        defaultLabelColor:"#000",
	        defaultLabelHoverColor:"#eee",
	        defaultHoverLabelBGColor:"#333",
	        labelSizeRatio:"1.25",
	        labelThreshold:12,
	        minEdgeSize:2,
	        maxEdgeSize:2,
	        minArrowSize:1,//This doesn't seem to change anything	 
	        edgeLabelSize: 'proportional'
	    }
	});
	
	var inverted = $("#inverted").val();
	var dataURL = baseURI+'/prov/data2';
	dataURL += "?roots="+root+"&inverted=true&travelUp=true&d=1";
	
	sigma.parsers.json(		
		dataURL, s,//?roots=ecf        
        function(){
			var zNode = null;

        	//var stats = s.graph.HITS();
			var i, nodes = s.graph.nodes(), edges = s.graph.edges(), len = nodes.length;   
            
            for(i = 0; i < edges.length; i++){
            	edge = edges[i];
            	edge.color = edgeColor;
            	edge.type = "arrow";
            	edge.size = 2;
            	//edge.size = s.graph.degree(edge.source,"out");
            }
                 
            for (i = 0; i < len; i++) {
            	//var stat = stats[node.id]
            	var node = nodes[i];
            	node.x = Math.random();
            	node.y = Math.random();

                var outDegree = s.graph.degree(node.id,"out");
            	var inDegree = s.graph.degree(node.id,"in");
                
                switch(node.nodeType){
                	case 0:
                		node.type = "circle";
                		break;
                	case "0":
                		node.type = "circle";
                		break;
                	case 1:
                		node.type = "square";
                    	break;
                	case "1":
                		node.type = "square";
                    	break;
                	case 2:
                		node.type = "diamond";
                    	break;
                	
                	case "2":
                		node.type = "diamond";
                    	break;
                }
                
                if(node.isCodebook === "true"){
                	node.type = "equilateral";
                	node.label += " (codebook)";
                }
                
                node.size = 3;//outDegree*3.0;//stat.hub * 550
               
                if(node.id == root){
                	node.expanded = true;
                	node.color = nodeColor;
                }else{  
                	node.expanded = false;
                	node.color = nodeColorLeaf;
                	/*
                	if(node.uri == null){
                		node.color = nodeNullURIColor;
	                }else if(inDegree === 0 && outDegree === 0){
	                	node.color = nodeDisconnectColor;
	                }else{
	                	
	                }*/
                }
                
                if($("#nodeFilterList option[value='"+node.id+"']").length < 1){
                	$("#nodeFilterList").append("<option value=\""+node.id+"\">"+node.label+"</option>");
                }
            }	
            
            if(preSelect){
             	$("#nodeFilterList option[value='"+root2+"']").prop('selected', true);
         	}
        	$("#graph2 h2").remove();   
            s.refresh();
            
            //TODO: adjust graph clustering settings
            //Build or cluster
            faConfig = {
            	barnesHutOptimize: true,	
        		barnesHutTheta:0.75,
        		linLogMode: true,
        		adjustSizes: false,
        		edgeWeightInfluence: 20,
        		scalingRatio:4,
        		strongGravityMode: true,
        		gravity: 1,
        		slowDown: 1000	            		 
            }; 

            //Push away
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

            s.configForceAtlas2(faConfig);
            s.startForceAtlas2();
  
            //Set graph to stop scaling after 750ms
            setTimeout(function(){ 
            	
            	graphBuilt = true;
            	s.stopForceAtlas2(); 
            	s.killForceAtlas2();
            	
            	$("#provShuffle").removeClass("disabled");	
            	//Change cursor on hovering node
            	s.bind('overNode outNode', function(e){
            		if(e.type === "overNode"){
            			$("#graph2").css("cursor","pointer");
            		}else{
            			$("#graph2").css("cursor","move");
            		}
            	});
    
            	var selected;
            	//Toggle highlight on click
            	s.bind('clickNode', function(e){
            		selected = select(s,e);	
                });

            	var rotating = false;
            	var keyRotateCW;
            	$(document).keydown(function(e){
            		//q key
            	    if(e.keyCode == 81 && !rotating){    	
            	    	rotating = true;
            	    	keyRotateCW = setInterval(function() {
        					rotate(s,0.01);
        				}, 5);
            	    }
            	}).keyup(function() {
        				clearInterval(keyRotateCW);
        				keyRotateCW = null;
        				rotating = false;
            	});
            	
            	var keyRotateCCW;
            	$(document).keydown(function(e){
            		//e key
            	    if(e.keyCode == 69 && !rotating){   
            	    	rotating = true;
            	    	keyRotateCCW = setInterval(function() {
        					rotate(s,-0.01);
        				}, 5);
            	    }
            	}).keyup(function() {
        				clearInterval(keyRotateCCW);
        				keyRotateCCW = null;
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
            	
            }, 750);
        }
	);
}

/**
 *Draws the graph
 */
function shuffle(){
	$("#provShuffle").click(function(){
		$("#graph2").empty();
		$("#graph2Info").empty();
		$("#graph2Info").css("padding","0em");
		buildGraph(startingRoot);	
	});
}

/**
 * Rotates the graph
 * @param sigma  s - graph instance
 * @param double delta - amount to rotate
 */
function rotate(s,delta){
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
 * Moves camera to a node
 * @param s
 * @param node
 */
function moveToNode(s,node){
	/*
	var zoom = node.size / 7.0;
	if(zoom > 1){
		zoom = 1;
	}else if(zoom < 0.5){
		zoom = 0.5
	}*/
	//,ratio:zoom
	s.camera.goTo({x:node['read_cam0:x'],y:node['read_cam0:y']});
	//console.log(zoom);
	return;
}

/**
 * Highlights a node and edges 
 * @param sigma  s - graph instance
 * @param event e - event from click
 * @param string id - id to select
 */
function select(s,e){
	viewingSubset = !viewingSubset;
 	var id = e.data.node.id;
 	var nodesAdded = 0;
 	
 	//If a node is selected
	if(selectedNode != e.data.node.id && selectedNode !== null){
		viewingSubset = true;
		resetGraph(s);
	}

	var show = viewingSubset;	
	
	//Fetches new nodes and edges
	if(!e.data.node.expanded){		
	 	$.ajax({
	        cache: false,
	        async: false,
	        type: "GET",
	        url: baseURI+"/prov/data2?roots="+id+"&inverted=true&travelUp=true&d=1",
	        complete: function(r){
	        	if(r.status < 400){
	        		var json = JSON.parse(r.responseText);
	        		var nodes = json.nodes;
	        		var edges = json.edges;
	        		for(var i in nodes){
	        			var node = nodes[i];
	        			//TODO: Size of nodes and position
	        			if(node.id != id && s.graph.nodes(node.id) === null){
	        				++nodesAdded;
	        				
	        				//var size = e.data.node.size;
		        			var size = e.data.node['read_cam0:size'];
		        			var xMin = e.data.node.x + size*-0.125;
		        			var xMax = e.data.node.x + size*0.125;
		        			var yMin = e.data.node.y + size*-0.125;
		        			var yMax = e.data.node.y + size*0.125;
		        			var x = Math.random() * (xMax - xMin) + xMin;
		        			var y = Math.random() * (yMax - yMin) + yMin;
		        			
	        				s.graph.addNode({
	    				      id: node.id,
	    				      label: node.label,
	    				      uri: node.uri,
	    				      date: node.date,
	    				      expanded: false,
	    				      nodeType: node.nodeType,
	    				      x: x,
	    				      y: y,
	    				      size: e.data.node.size,
	    				      color:nodeColorLeaf
	    				    });
	        			}	
	        		}
	        		
	        		for(var k in edges){
	        			var edge = edges[k];
	        			if(s.graph.edges(edge.id) === null){
	        				s.graph.addEdge({
	        			        id: edge.id,
	        			        source: edge.source,
	        			        target: edge.target,
	        			        edgeType: edge.edgeType,
	        			        color: edgeColor,
	        					type: "arrow",
	        					size: 2
	        					//size : s.graph.degree(edge.source,"out")
	        			    });
	        			}
	        		}
	        	}
	        }
	    });
	 	e.data.node.expanded = true;
	}

	var config = nodesAdded > 0 ? faConfig : faConfig2;
 	var timeout = 500;//Time to redraw
 	
	if(show){
		selectedNode = id;
		e.data.node.color = selectColor;
		drawInfoBox(s,e);
		redraw(s,config,timeout);
		return e;
	}else{	
		selectedNode = null;
		e.data.node.color = nodeColor;
		resetGraph(s);
		redraw(s,config,timeout);
		return null;
	}	
}

function redraw(s,config,timeout){
	//Redraw the graph
 	s.configForceAtlas2(config);
	s.startForceAtlas2();
	setTimeout(function() { 
     	s.stopForceAtlas2(); 
     	s.killForceAtlas2();
    }, timeout);	
}

function drawInfoBox(s,e){
	e.data.node.color = selectColor;

	//Draws custom Really Fancy© info box
	var info = "ID: "+ e.data.node.id;
	info += "<br /> Label: "+ e.data.node.label;

	var link = "";
	if(e.data.node.uri !== null){
		var uri = e.data.node.uri;
		link ="URI: <a href='"+uri+"' target='_blank'>"+uri+"</a>";
	}else{
		link = "URI: none";
	}
	info += "<br />"+link;
	
	switch(e.data.node.nodeType){
		case 0:
			info += "<br /> Type: Entity";
			break;
		case "0":
			info += "<br /> Type: Entity";
			break;
		case 1:
			info += "<br /> Type: Agent";
	    	break;
		case "1":
			info += "<br /> Type: Agent";
	    	break;
		case 2:
			info += "<br /> Type: Activity";
	    	break;	
		case "2":
			info += "<br /> Type: Activity";
	    	break;
	}
	
	
	info += "<br /><a href='"+baseURI+"/edit/prov/"+e.data.node.id+"'>Edit node</a>";
	info = "<p>"+info+"</p>";
	$("#graph2Info").html(info);
	$("#graph2Info").css("padding","0.5em 1em");	
}


/**
 * Deselects everything
 * @param s
 */
function resetGraph(s){
	var nodes = s.graph.nodes();
	var edges = s.graph.edges();

	for (i = 0; i < edges.length; i++){
		edges[i].color=edgeColor;
	}
	
	for (i = 0; i < nodes.length; i++){
		var node = nodes[i];
		var outDegree = s.graph.degree(node.id,"out");
     	var inDegree = s.graph.degree(node.id,"in");
		node.hidden = false;

		if(node.expanded){
			node.color = nodeColor;
		}else{
			node.color = nodeColorLeaf;
		}
	}
	$("#graph2Info").empty();
	$("#graph2Info").css("padding","0em");
}