window.onload = function(){

	var socket = new WebSocket("ws://localhost:8080/web");
	var status = document.querySelector("#status");
	var key = true;

	socket.onopen = function(event){
		//status.innerHTML = "Соединение установленно";
	}
	
	socket.onclose = function(event){
		if (event.wasClean){
			status.innerHTML = 'соединение закрыто';
		}
		else{
			status.innerHTML = 'соединение закрылось некоректно <br />';
			status.innerHTML = event.code + "Error : " + event.reason;
		}
	}
	socket.onmessage = function(event){
		status.appendChild(getMessageBlock(event.data));
		status.scrollTop = status.scrollHeight;
	}

	socket.onerror = function(event){
		status.innerHTML = event.code + "Error : " + event.reason;
	}


	var form = document.forms["chat"];
	form.onsubmit = function(){
		var message = this.message.value;
		if(message.length > 0){
			if(!checkCommand(message)){
				status.appendChild(getMessageBlock(message));
				status.scrollTop = status.scrollHeight;
			}
			socket.send( message);
		}
		this.message.value = "";
		return false;
	}
	
	getMessageBlock = function(message){
		var div = document.createElement("div");
		
		if(key){
			div.setAttribute("class","messageBlock rightMessage");
		}else{
			div.setAttribute("class","messageBlock leftMessage");
		}
		
		key = !key;
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