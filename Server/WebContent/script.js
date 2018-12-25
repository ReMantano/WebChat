window.onload = function(){
	var URL = "ws://localhost:8080/Server/web";

	var QUERY_PROFILE = "profile";
	var QUERY_NAME = "name";
	var QUERY_COUNT = "count";
	
	var CLASS_CHAT_MAIN = "chatBox";
	var CLASS_CHAT_BLOCK = "status";
	var CLASS_CHAT_HEADER = "header";
	var CLASS_CHAT_HEADER_NAME = "headerChatName";
	var CLASS_CHAT_HEADER_BUTTON = "deleteChatBlockButton";
	var CLASS_CHAT_FORM = "ChatForm"
	var CLASS_CHAT_INPUT_FIELD = "ChatTextField"
	var CLASS_CHAT_MESSAGE_BLOCK = "messageBlock ";
	var CLASS_CHAT_MESSAGE_BLOCK_LEFT = "leftMessage";
	var CLASS_CHAT_MESSAGE_BLOCK_RIGHT = "rightMessage";
	var NAME_TABLE = "mainTable";
	var NAME_CHAT_FORM = "chat";
	var NAME_CHAT_BLOCK = "block";
	var NAME_CHAT_INPUT_FIELD = "message";
	var NAME_CHAT_HEADER_BUTTON_DELETE = "buttonDelete";
	var NAME_CHAT_HEADER_BUTTON_LEAVE = "buttonLeave";
	var NAME_CHAT_HEADER_NAME = "headerName";

	var socket = new WebSocket(URL);
	var chatField = document.getElementById(NAME_CHAT_BLOCK);
	var table = document.getElementById(NAME_TABLE);
	var formChat = document.forms[NAME_CHAT_FORM];
	
	var chatNumber, profile, name, count;
	var connection = true;


	

	getMessageBlock = function(message,key){
		var div = document.createElement("div");

		if(key){
			div.setAttribute("class",CLASS_CHAT_MESSAGE_BLOCK + " " + CLASS_CHAT_MESSAGE_BLOCK_RIGHT);
		}else{
			div.setAttribute("class",CLASS_CHAT_MESSAGE_BLOCK + " " + CLASS_CHAT_MESSAGE_BLOCK_LEFT);
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
		div.setAttribute("class",CLASS_CHAT_MAIN);
		
		var divChatField = document.createElement("div");
		divChatField.setAttribute("class",CLASS_CHAT_BLOCK);
		divChatField.setAttribute("id",NAME_CHAT_BLOCK + id);
		
		var chatForm = document.createElement("form");
		chatForm.setAttribute("name",NAME_CHAT_FORM + id);
		chatForm.setAttribute("action","#");
		chatForm.setAttribute("class",CLASS_CHAT_FORM);
		
		var inputFiled = document.createElement("input");
		inputFiled.setAttribute("name",NAME_CHAT_INPUT_FIELD);
		inputFiled.setAttribute("type","text");
		inputFiled.setAttribute("class",CLASS_CHAT_INPUT_FIELD);
		
		var divHeader = document.createElement("div");
		divHeader.setAttribute("class",CLASS_CHAT_HEADER);
		
		var headerButton = document.createElement("button");
		headerButton.setAttribute("class",CLASS_CHAT_HEADER_BUTTON);
		headerButton.setAttribute("name",NAME_CHAT_HEADER_BUTTON_DELETE + id);
		headerButton.innerHTML = "Delete";
		
		headerButton.onclick = function(){
			var test = document.getElementById(id);
			test.parentNode.removeChild(test);
			
			let jMessage = {
				Command: "DELETE",
				Index: id.toString()
			}
			
			socket.send(JSON.stringify(jMessage));
		}
		
		var h1 = document.createElement("h1");
		h1.setAttribute("class", CLASS_CHAT_HEADER_NAME);
		h1.setAttribute("id", NAME_CHAT_HEADER_NAME + id);
		h1.innerHTML = "Chat";
		
		divHeader.appendChild(h1);
		divHeader.appendChild(headerButton);
		
		chatForm.appendChild(inputFiled);
		div.appendChild(divHeader);
		div.appendChild(divChatField);
		div.appendChild(chatForm);
		
		var td = document.createElement("td");
		td.setAttribute("id",id);
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

		
	startChat = function(){
		profile = getQueryVariable(QUERY_PROFILE);
		name =  getQueryVariable(QUERY_NAME);

		if(profile == "Agent"){
			count = getQueryVariable(QUERY_COUNT);
		}else{
			count = "1";
		}
		
		for (var i = 1; i < count; i++){
			crateNewChatBlock(i);
			var f = document.forms[NAME_CHAT_FORM + i];
			f.onsubmit = function(){
			var message = this.message.value;
			
			let jMessage = {
				Message: message,
				Name: name,
				Command: "TEXT",
				Index: this.name.substring(NAME_CHAT_FORM.length)
			}
			
			if (connection){

				if(message.length > 0){
					chatNumber = this.name.substring(NAME_CHAT_FORM.length);
					if(!checkCommand(message)){
						var block = document.getElementById(NAME_CHAT_BLOCK + chatNumber);
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
	}
	
	
	socket.onopen = function(){
		startChat();
		
		let jMessage = {
			Name: name,
			Command: "REGISTER",
			Status: profile,
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
		var block, nameHeader = "Chat";
		var ind = jMessage.Index;
		
		if(jMessage.Name != undefined){
			nameHeader = jMessage.Name;
		}
		
		if (ind == undefined){
			block = document.getElementById("block");
				for(var i = 1; i < parseInt(count) + 1; ++i){
					block.appendChild(getMessageBlock(jMessage.Message,false));
		            block.scrollTop = block.scrollHeight;
					block = document.getElementById("block" + i);
				}
		}else{
			if(parseInt(ind) > 0 && count != "1"){
				document.getElementById(NAME_CHAT_HEADER_NAME + ind).innerHTML = nameHeader;
				block = document.getElementById("block" + ind);
			}else{
					document.getElementById(NAME_CHAT_HEADER_NAME).innerHTML = nameHeader;
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

	document.getElementById(NAME_CHAT_HEADER_BUTTON_LEAVE).onclick = function(){
		let jMessage = {
			Command: "LEAVE",
			Index: "0"
		}
		
		socket.send(JSON.stringify(jMessage));
	}
	
}