/**
 * http://usejsdoc.org/
 */

function toMesage(body, Message) {
var message =  new Message(
{
	MessageId: body.MessageId,
	From: body.From,
	To: body.To,
	Message: body.Message,
	DateofMsg: body.DateofMsg,
	DeliveredToAll: body.DeliveredToAll,
	DeliveredSuccessfullyTo: body.DeliveredSuccessfullyTo,
	DeliveredFailedTo: body.DeliveredFailedTo,
	
});

return message;
}

exports.createMessage = function (model, requestBody, response)
{
	var message = toMesage(requestBody, model);
	
	message.save(function(err){
		if (err)
			{
			throw err;
			}
		console.log('message saved successfully');
		response.end('done saving message');
	});
}

exports.findMessagebyId = function (model, _messageId, response) {
	model.findOne({MessageId: _messageId},
	function(error, result) {
	if (error) {
	console.error(error);
	response.writeHead(500,
	{'Content-Type' : 'text/plain'});
	response.end('Internal server error');
	return;
	} else {
	if (!result) {
	if (response != null) {
	response.writeHead(404, {'Content-Type' : 'text/plain'});
	response.end('Message Not Found');
	}
	return;
	}
	if (response != null){
	response.setHeader('Content-Type', 'application/json');
	response.send(result);
	}
	//console.log(result);
	}
	});
	}

exports.listMessages = function (model, response) {
	model.find({}, function(error, result) {
	if (error) {
	console.error(error);
	return null;
	}
	if (response != null) {
	response.setHeader('content-type', 'application/json');
	response.end(JSON.stringify(result));
	}
	return JSON.stringify(result);
	});
	}

exports.deleteMessage = function (model, _messageId, response)
{
console.log('Deleting Message from: ' + _messageId);
model.findOne({MessageId: _messageId},
function(error, data) {
if (error) {
console.log(error);
if (response != null) {
response.writeHead(500, {'Content-Type' : 'text/plain'});
response.end('Internal server error');
}
return;
} else {
if (!data) {
console.log('Message not found');
if (response != null) {
response.writeHead(404,
{'Content-Type' : 'text/plain'});
response.end('Message Not Found');
}
return;
} else {
data.remove(function(error){
if (!error) {
data.remove();
}
else {
console.log(error);
}
});
if (response != null){
	response.send('Deleted Message');
	}
	return;
	}
	}
	});
	}

exports.updateMessage = function (model, requestBody, response) {
	var messageId = requestBody.MessageId;
	model.findOne({MessageId: messageId},
	function(error, data) {
	if (error) {
	console.log(error);
	if (response != null) {
	response.writeHead(500,
	{'Content-Type' : 'text/plain'});
	response.end('Internal server error');
	}
	return;
	} else {
	var message = toMesage(requestBody, model);
	if (!data) {
	console.log('Message Id: '+ messageId
	+ ' does not exist. The Message will be created.');
	message.save(function(error) {
	if (!error)
		message.save();
	});
	if (response != null) {
	response.writeHead(201,
	{'Content-Type' : 'text/plain'});
	response.end('Created');
	}
	return;
	}
	//poulate the document with the updated values
	data.MessageId = message.MessageId;
	data.From = message.From;
	data.To = message.To;
	data.Message = message.Message;
	data.DateofMsg = message.DateofMsg;
	data.DeliveredToAll = message.DeliveredToAll;
	data.DeliveredSuccessfullyTo = message.DeliveredSuccessfullyTo;
	data.DeliveredFailedTo = message.DeliveredFailedTo;
	
	
	// now save
	data.save(function (error) {
	if (!error) {
	console.log('Successfully updated Message with Message Id: '+ messageId);
	data.save();
	} else {
	console.log('error on save');
	}
	});
	if (response != null) {
	response.send('Updated');
	}
	}
	});
};

exports.findMessagesFrom = function (model, from, response){
	model.find({From: from}, function(error, result) {
		if (error) {
		console.error(error);
		return null;
		}
		if (response != null) {
		response.setHeader('content-type', 'application/json');
		response.end(JSON.stringify(result));
		}
		return JSON.stringify(result);
		});
}
