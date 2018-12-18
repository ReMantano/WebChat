window.onload = function(){


	var socket = new WebSocket("ws://localhost:8080/Server/web");
	var chatField = document.querySelector("#block");
	var table = document.getElementById("mainTable");
	var connection = true;
	var chatNumber;

	socket.onopen = function(){
		let jMessage = {
			Name: name,
			Command: "REGISTER",
			Status: prof,
			Size: count
		}
		socket.send(JSON.stringify(jMessage));
	}

	socket.onclose = function(event){
		var div = document.createElement("div");

		div.innerHTML = 'соединение закрыто';
		div.setAttribute("class","systemMessage");
		chatField.appendChild(div);
		chatField.scrollTop = chatField.scrollHeight;
		connection = false;
	}
	socket.onmessage = function(event){
		let jMessage = JSON.parse(event.data);
		var block;
		var ind = jMessage.Index;
		if (ind == undefined){
			block = document.getElementById("block");
				for(var i = 1; i < parseInt(count) + 1; ++i){
					block.appendChild(getMessageBlock(jMessage.Message,false));
		            block.scrollTop = block.scrollHeight;
					block = document.getElementById("block" + i);
				}
		}else{
			if(parseInt(ind) > 0 && count != "1"){
				block = document.getElementById("block" + ind);
			}else{
					block = chatField;
			}
			
			block.appendChild(getMessageBlock(jMessage.Message,false));
			block.scrollTop = block.scrollHeight;
		}
	

	}

	socket.onerror = function(event){
		var div = document.createElement("div");
		div.innerHTML = "Ошибка соединения: " + event.reason;
		div.setAttribute("class","systemMessage");
		chatField.appendChild(div);
		chatField.scrollTop = chatField.scrollHeight;


		connection = false;
	}


	var formChat = document.forms["chat"];
	formChat.onsubmit = function(){
		let jMessage = {
			Message: this.message.value,
			Name: name,
			Command: "TEXT",
			Index: "0"
		}
		
		if (connection){
			var message = this.message.value;
			if(message.length > 0){
				chatNumber = null;
				if(!checkCommand(message)){
					chatField.appendChild(getMessageBlock(message,true));
					chatField.scrollTop = chatField.scrollHeight;
				}else{
					jMessage.Command = this.message.value.substr(1);
				}
				socket.send( JSON.stringify(jMessage));
				
			}
		}
		this.message.value = "";
		return false;
	}
	

	getMessageBlock = function(message,key){
		var div = document.createElement("div");

		if(key){
			div.setAttribute("class","messageBlock rightMessage");
		}else{
			div.setAttribute("class","messageBlock leftMessage");
		}

		div.innerHTML = message;
		
		return div;
	}
	
	checkCommand = function(message){
		if (message[0] == '\\'){
			return true;
		}
		else {
			return false;
		}
	}
	
	crateNewChatBlock = function(id){
	
		var div = document.createElement("div");
		div.setAttribute("class","chatBox");
		var divChatField = document.createElement("div");
		divChatField.setAttribute("class","status");
		divChatField.setAttribute("id","block" + id);
		
		var chatForm = document.createElement("form");
		chatForm.setAttribute("name","chat" + id);
		chatForm.setAttribute("action","#");
		chatForm.setAttribute("class","ChatForm");
		
		var inputFiled = document.createElement("input");
		inputFiled.setAttribute("name","message");
		inputFiled.setAttribute("type","text");
		inputFiled.setAttribute("class","ChatTextField");
		
		var h1 = document.createElement("h1");
		h1.innerHTML = "Chat";
		
		chatForm.appendChild(inputFiled);
		div.appendChild(h1);
		div.appendChild(divChatField);
		div.appendChild(chatForm);
		
		var td = document.createElement("td");
		td.appendChild(div);
		
		if(id % 2 != 0){;
			table.rows[table.rows.length -1].appendChild(td);
		}else{
			var tr = document.createElement("tr");
			tr.appendChild(td);
			table.appendChild(tr);
		}
		
		//document.body.appendChild(div);
	}
	
	
	function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split('&');
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split('=');
        if (decodeURIComponent(pair[0]) == variable) {
            return decodeURIComponent(pair[1]);
        }
    }
		console.log('Query variable %s not found', variable);
	}

	var prof = getQueryVariable("profile");
	var count = getQueryVariable("count");
	var name =  getQueryVariable("name");
	
	for (var i = 1; i < count; i++){
		crateNewChatBlock(i);
		var f = document.forms["chat" + i];
		f.onsubmit = function(){
		let jMessage = {
			Message: this.message.value,
			Name: name,
			Command: "TEXT",
			Index: this.name.substring(4)
		}
		if (connection){
			var message = this.message.value;
			if(message.length > 0){
				chatNumber = this.name.substring(4);
				if(!checkCommand(message)){
					var block = document.getElementById("block" + chatNumber);
					block.appendChild(getMessageBlock(message,true));
					block.scrollTop = chatField.scrollHeight;
				}else{
					jMessage.Command = this.message.value.substr(1);
				}
					socket.send( JSON.stringify(jMessage));
				
			}
		}
		this.message.value = "";
		return false;
	}
	}
	 getIndexFormMessage = function(message){
		 var temp =  message.substring(message.indexOf("%"));
		 return temp.substring(message.indexOf("%"));
	 }
	

	
}