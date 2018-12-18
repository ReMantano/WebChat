crateNewChatBlock = function(id){
	
	var div = document.createElement("div");
	div.setAttribute("class","chatBox");
	var divChatField = document.createElement("div");
	divChatField.setAttribute("class","status");
	divChatField.setAttribute("id","block" + id);
	
	var chatForm = document.createElement("form");
	chatForm.setAttribute("name","chat" + id);
	chatForm.setAttribute("class","ChatForm");
	
	var inputFiled = document.createElement("input");
	inputFiled.setAttribute("name","message");
	inputFiled.setAttribute("type","text");
	inputFiled.setAttribute("class","ChatTextField");
	
	chatForm.appendChild(inputFiled);
	div.appendChild(divChatField);
	div.appendChild(chatForm);
}