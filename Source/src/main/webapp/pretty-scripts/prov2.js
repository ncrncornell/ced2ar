var viewingSubset = false;
var selectedNode = null;

var zoomMin = 0.1;
var zoomMax = 1.5;

var selectColor = "#4282ca";
var nodeColor = "#2a6496";
var nodeNullURIColor = "#333"; 
var nodeDisconnectColor = "rgba(42,100,150,.25)";
	
var edgeColor = "#ccc";
var outColor = "#d91616";
var inColor = "#00d093";
var srColor = "#d97816";

$(document).ready(function(){
	$("#graph2").html("<h2>Loading...</h2>");
	graphBuilt = false;
	lastProvS = "";
	baseURI = $('#meta_uri').html();
	buildGraph();
	shuffle();
});


function buildGraph(){
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
	        
	        /*
	        borderSize:3,
	        nodeHoverColor:"default",
	        defaultNodeBorderColor:"#193c5a",
	       	*/

	        defaultLabelColor:"#000",
	        defaultLabelHoverColor:"#eee",
	        defaultHoverLabelBGColor:"#333",
	        labelSizeRatio:"1.25",
	        labelThreshold:10,
	        minEdgeSize:0,
	        maxEdgeSize:0
	    }
	});
	
	var root = $("#nodeFilterList :selected").val();
	var root2 = $("#filterNode").val();
	var inverted = $("#inverted").val();
	var dataURL = baseURI+'/prov/data2';
	if(root !== null && root !== "" ){
		dataURL += "?roots="+root;
		if(inverted !== null && inverted !== ""){
			dataURL += "&inverted=true&travelUp=true";
		}
	}else if(root2 !== null && root2 !== "" && !graphBuilt){
		preSelect = true; 
		dataURL += "?roots="+root2;
		if(inverted !== null && inverted !== ""){
			dataURL += "&inverted=true&travelUp=true";
		}
	}else if(inverted !== null && inverted !== ""){
		lastProvS = "";
		dataURL += "?inverted=true";
	}
	
	sigma.parsers.json(		
		dataURL, s,//?roots=ecf        
        function(){
			var zID = $("#zoomOn").val();
			var zNode = null;

        	//var stats = s.graph.HITS();
			var i, nodes = s.graph.nodes(), edges = s.graph.edges(), len = nodes.length;   
            
            for(i = 0; i < edges.length; i++){
            	edge = edges[i];
            	edge.color = "#ccc";
            	edge.type = "curvedArrow";
            	edge.size = 2;
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
                
                node.size = outDegree*3.0;//stat.hub * 550
               
                if(zID !== "" && node.id == zID){
            		zNode = node;
            		node.color = "#F00";
            	}else if(node.uri === null){
                	node.color = nodeNullURIColor;
                }else if(inDegree === 0 && outDegree === 0){
                	node.color = nodeDisconnectColor;
                }else{
                	node.color = nodeColor;
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
            
            //Build or cluster
            faConfig = {
                	barnesHutOptimize: true,	
            		barnesHutTheta:0.5,
            		linLogMode: false,
            		adjustSizes: false,
            		edgeWeightInfluence: 10,
            		scalingRatio:1,
            		strongGravityMode: true,
            		gravity: 0,
            		slowDown: 100	            		 
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

            s.configForceAtlas2(faConfig);
            s.startForceAtlas2();
  
            //Set graph to stop scaling after 750ms
            setTimeout(function(){ 
            	
            	graphBuilt = true;
            	s.stopForceAtlas2(); 
            	s.killForceAtlas2();
            	
            	if(zNode !== null){
            		moveToNode(s,zNode);
            		$("#zoomOn").val("");
            	}
            	
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
		buildGraph();	
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

	var nodes = s.graph.nodes();
	var edges = s.graph.edges();
 	var id = e.data.node.id;

 	//If a node is selected
	if(selectedNode != e.data.node.id && selectedNode !== null){
		viewingSubset = true;
		resetGraph(s);
	}
	
	var show = viewingSubset;	
 	
 	var selectedNodes = [];
 	var incomingEdgeCount = 0;
 	var outgoingEdgeCount = 0;
 	selectedNodes.push(id);

	if(show){
		selectedNode = id;
		e.data.node.color = selectColor;
		for (i = 0; i < edges.length; i++){
	 		var edge = edges[i];
     		if(edge.source === id || edge.target === id){
     			//Logic needed b/c target can equal source
     			if(edge.source === id){
         			outgoingEdgeCount++;
         			selectedNodes.push(edge.target);
         			edge.color=outColor;
         		}
     			if(edge.target === id){
         			incomingEdgeCount++;
         			selectedNodes.push(edge.source);
         			edge.color=inColor;  
     			}	
     			if(edge.target == edge.source){
     				edge.color=srColor;  
     			}
     		}else{            
     			edge.color="transparent";   
     		}
	 	}
		
		for(i = 0; i < nodes.length; i++){
	 		var node = nodes[i];
	 		if(selectedNodes.indexOf(node.id) == -1){
	 			node.hidden = true;
	 		}
		}
	 		
		//moveToNode(s,e.data.node);
		
		//Draws custom Really Fancy© info box
		var info = "ID: "+ e.data.node.id;
		info += "<br /> Label: "+ e.data.node.label;
		//info += "<br /> Indegree: "+ String(incomingEdgeCount);
		//info += "<br /> Outdegree: "+ String(outgoingEdgeCount);
		var link = "";
		if(e.data.node.uri !== null){
			var uri =e.data.node.uri;
			link ="URI: <a href='"+uri+"' target='_blank'>"+uri+"</a>";
		}else{
			link = "URI: none";
		}
		info += "<br />"+link;
		info += "<br /><a href='"+baseURI+"/edit/prov/"+e.data.node.id+"'>Edit this node</a>";
		info = "<p>"+info+"</p>";
		$("#graph2Info").html(info);
		$("#graph2Info").css("padding",".5em 1em");	

	}else{	
		selectedNode = null;
		e.data.node.color = nodeColor;
		resetGraph(s);
	}
 	
 	var c = faConfig2;
 	var t = 1000;

 	//Redraw the graph
 	s.configForceAtlas2(c);
	s.startForceAtlas2();
	setTimeout(function() { 
     	s.stopForceAtlas2(); 
     	s.killForceAtlas2();
    }, t);	

	if(show){
		return e;
	}
	
	return null;
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
		if(node.uri === null){
			node.color = nodeNullURIColor;
		}else if(inDegree === 0 && outDegree === 0){
        	node.color = nodeDisconnectColor;
        }else{
			node.color = nodeColor;
		}
	}
	$("#graph2Info").empty();
	$("#graph2Info").css("padding","0em");
}