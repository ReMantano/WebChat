window.onload = function(){

	var socket = new WebSocket("ws://localhost:8080/web");
	var chatField = document.querySelector("#status");
	var connection ;

	socket.onopen = function(event){
		//chatField.innerHTML = "Соединение установленно";
		connection = true;
	}

	socket.onclose = function(event){
		var div = document.createElement("div");

		div.innerHTML = 'соединение закрыто';
		div.setAttribute("class","messageBlock");
		chatField.appendChild(div);
		chatField.scrollTop = chatField.scrollHeight;
		connection = false;
	}
	socket.onmessage = function(event){
		chatField.appendChild(getMessageBlock(event.data,false));
		chatField.scrollTop = chatField.scrollHeight;
	}

	socket.onerror = function(event){
		var div = document.createElement("div");
		div.innerHTML = "Ошибка соединения: " + event.reason;
		div.setAttribute("class","messageBlock");
		chatField.appendChild(div);
		chatField.scrollTop = chatField.scrollHeight;


		connection = false;
	}


	var form = document.forms["chat"];
	form.onsubmit = function(){
		if (connection){
			var message = this.message.value;
			if(message.length > 0){
				if(!checkCommand(message)){
					chatField.appendChild(getMessageBlock(message,true));
					chatField.scrollTop = chatField.scrollHeight;
				}
				socket.send( message);
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
}