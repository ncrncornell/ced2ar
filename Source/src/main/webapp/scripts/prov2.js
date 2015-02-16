$(document).ready(function(){	  
	graphBuilt = false;
	lastProvS = "";
	baseURI = $('#meta_uri').html();
	buildGraph();
	shuffle();
});


function buildGraph(){
	//Resize graph
	var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0) * 0.52;
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
	        zoomMax:1.5,
	        minNodeSize: 8,
	        maxNodeSize: 20,
	        
	        /*
	        borderSize:3,
	        nodeHoverColor:"default",
	        defaultNodeBorderColor:"#193c5a",
	       	*/
	        
	        defaultLabelColor:"#000",
	        defaultLabelHoverColor:"#eee",
	        defaultHoverLabelBGColor:"#333",
	        labelSizeRatio:"1.25",
	        	
	        minEdgeSize:3,
	        maxEdgeSize:3
	    
	    }
	});
	
	var root = $("#nodeFilterList :selected").val();
	var root2 = $("#filterNode").val();
	var inverted = $("#inverted").val();
	var dataURL = baseURI+'/prov/data2';
	root
	if(root != null && root != "" ){
		dataURL += "?roots="+root;
		if(inverted != null && inverted != ""){
			dataURL += "&inverted=true";
		}
	}else if(root2 != null && root2 != "" && !graphBuilt){
		preSelect = true; 
		dataURL += "?roots="+root2;
		if(inverted != null && inverted != ""){
			dataURL += "&inverted=true";
		}
	}else if(inverted != null && inverted != ""){
		lastProvS = "";
		dataURL += "?inverted=true";
	}
	
	sigma.parsers.json(		
		dataURL, s,//?roots=ecf        
        function(){
			var zID = $("#zoomOn").val();
			var zNode = null;

        	//var stats = s.graph.HITS();
            var i, nodes = s.graph.nodes(), len = nodes.length;            
                 
            for (i = 0; i < len; i++) {
            	//var stat = stats[nodes[i].id]
                nodes[i].x = Math.random();
                nodes[i].y = Math.random();
                switch(nodes[i].nodeType){
                	case 0:
                		nodes[i].type = "circle";
                		break;
                	case "0":
                		nodes[i].type = "circle";
                		break;
                	case 1:
                		nodes[i].type = "square";
                    	break;
                	case "1":
                		nodes[i].type = "square";
                    	break;
                	case 2:
                		nodes[i].type = "diamond";
                    	break;
                	
                	case "2":
                		nodes[i].type = "diamond";
                    	break;
                }
                
                nodes[i].size = s.graph.degree(nodes[i].id,"out")*3.0;//stat.hub * 550
               
                if(zID !== "" && nodes[i].id == zID){
            		zNode = nodes[i];
            		nodes[i].color = "#F00";
            	}else if(nodes[i].uri == null){
                	nodes[i].color = "#333";
                }else{
                	nodes[i].color = "#2a6496";
                }
                if($("#nodeFilterList option[value='"+nodes[i].id+"']").length < 1){
                	$("#nodeFilterList").append("<option value=\""+nodes[i].id+"\">"+nodes[i].label+"</option>");
                }
            }	
            
            if(preSelect){
             	$("#nodeFilterList option[value='"+root2+"']").prop('selected', true);
         	}
               
            s.refresh();
            
            //Build or cluster
            faConfig = {
            	barnesHutOptimize: true,	
        		barnesHutTheta:.5,
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

            s.configForceAtlas2(faConfig)
            s.startForceAtlas2();
  
            //Set graph to stop scaling after 1500ms
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
            		selected = select(s,e,false);	
                });
            	
            	//Repaints if nodes are moved back on screen
            	s.bind('clickStage', function(e){
            		if(selected){
            			select(s,selected,true)
            		}
            	});
            	
            	//Repaints if nodes are moved back on screen via zoom
            	var zoomLevel = s.camera.ratio;
            	var zoomAdjust = true;
            	watch(s.camera,"ratio",function(){
        			setTimeout(function(){ 
        				if(s.camera.ratio == zoomLevel){
        					if(!zoomAdjust){
        						if(selected){
        	            			select(s,selected,true)
        	            		}
        						zoomAdjust = true;
        					}
            	     	}else{
            	     		zoomLevel = s.camera.ratio;
            	     		zoomAdjust = false;
            	     	}
        			},100);	 
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
            	    }3
            	}).keyup(function() {
        				clearInterval(keyRotateCCW);
        				keyRotateCCW = null;
        				rotating = false;
            	});    	
            }, 1500);
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
	//console.log("ratio:" + cR + " angle:" + cA + " x:" + s.camera.x + "y:"+s.camera.y);
}

/**
 * Moves camera to a node
 * @param s
 * @param node
 */
function moveToNode(s,node){
	var zoom = node.size / 7.0;
	if(zoom > 1){
		zoom = 1;
	}else if(zoom < 0.5){
		zoom = 0.5
	}
	s.camera.goTo({x:node['read_cam0:x'],y:node['read_cam0:y'],ratio:zoom})
	//console.log(zoom);
	return;
}

/**
 * Highlights a node and edges 
 * @param sigma  s - graph instance
 * @param event e - event from click
 * @param string id - id to select
 */
function select(s,e,r){
	
	var nodes = e.data.renderer.nodesOnScreen;
 	var edges = e.data.renderer.edgesOnScreen;
 	var id = e.data.node.id;
 	
 	var outColor = "#d91616";
 	var inColor = "#00d093";
 	var srColor = "#d97816";
 	var nsColor = "rgba(204,204,204,0.5)";
 	var nsColor2 = "rgba(204,204,204,0.25)";
 	
 	var selectColor = "#4282ca";
 	var nodeColor = "#2a6496";
 	var nodeOffNetwork = "#333";
 	
 	var selectedNodes = [];
 	var incomingEdgeCount = 0;
 	var outgoingEdgeCount = 0;
 	selectedNodes.push(id);
 	
 	//If node is off network, select color is different
 	if(e.data.node.uri == null){
 		selectColor = "#555";
 	}
 	
 	var show = true;	
 	//if selecting, show = true, else false
 	if(e.data.node.color === selectColor && !r){
 		show = false;
 		//If deselected node is off network, change back to off network color
 		if(e.data.node.uri == null){
 			e.data.node.color = nodeOffNetwork;
 	 	}else{
 	 		e.data.node.color = nodeColor;	
 	 	}
 	}else{
 		e.data.node.color = selectColor;
 	}
 	
 	if(!r && show){
		moveToNode(s,e.data.node);
	}
 	
 	//Recolor all edges
 	for (i = 0; i < edges.length; i++){
 		var edge = edges[i];
 		if(show){
     		if(edge.source === id || edge.target === id){
     			//Logic needed b/c target can equal source
     			if(edge.source === id){
         			outgoingEdgeCount++;
         			selectedNodes.push(edge.target);
         			edge.color=outColor;
         			//edge.weight=5;
         		}
     			if(edge.target === id){
         			incomingEdgeCount++;
         			selectedNodes.push(edge.source);
         			edge.color=inColor;  
         			//edge.weight=5;
     			}	
     			if(edge.target == edge.source){
     				edge.color=srColor;  
     				//edge.weight=5;	
     			}
     		}else{            
     			edge.color="transparent";   
     		}
 		}else{
 			edge.color=nsColor;
 			/*
 			if(edge.weight == 5){
 				edge.weight == -10;
 			}else{
 				edge.weight=1;
 			}
 			*/
 		}
 	}
 	
 	//Recolor non-selected nodes
 	for(i = 0; i < nodes.length; i++){
 		var node = nodes[i];
 		var nc = nodeColor;

 		if(node.uri == null){
 			nc = nodeOffNetwork;
 		}
 		
 		if(selectedNodes.indexOf(node.id) == -1){
 			if(show){
 				node.color = nsColor2;
 			}else{
 				node.color = nc;
 			}
 		}else if(node.id != id){
 			node.color=nc;
 		}
 	}
 	
 	//Draws custom Really Fancy© info box
	if(show){
		var info = "ID: "+ e.data.node.id;
		info += "<br /> Label: "+ e.data.node.label;
		info += "<br /> Indegree: "+ String(incomingEdgeCount);
		info += "<br /> Outdegree: "+ String(outgoingEdgeCount);
		var link = "";
		if(e.data.node.uri != null){
			var uri =e.data.node.uri;
			link ="URI: <a href='"+uri+"' target='_blank'>"+uri+"</a>";
		}else{
			link = "URI: none";
		}
		info += "<br />"+link;
		info += "<br /><a href='"+baseURI+"/edit/prov/"+e.data.node.id+"'>Details Page</a>";
		info = "<p>"+info+"</p>";
		$("#graph2Info").html(info);
		$("#graph2Info").css("padding",".5em 1em");	
	}else{
		$("#graph2Info").empty();
		$("#graph2Info").css("padding","0em");
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