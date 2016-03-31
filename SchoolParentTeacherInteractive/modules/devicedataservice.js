/**
 * http://usejsdoc.org/
 */
function toDevice(body, MobileDevice) {
var mobiledevice =  new MobileDevice(
{
	MobileNumber: body.MobileNumber,
	DeviceId: body.DeviceId
});

return mobiledevice;
}

exports.createMobileDevice = function (model, requestBody, response)
{
	var mobiledevice = toDevice(requestBody, model);
	
	mobiledevice.save(function(err){
		if (err)
			{
			throw err;
			}
		console.log('Mobile Device saved successfully');
		response.end('Mobile Device saved');
	});
}

exports.findDeviceByMobileNumber = function (model, _mobileNumber, response) {
	model.findOne({MobileNumber: _mobileNumber},
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
	response.end('Device Not Found');
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

exports.listDevices = function (model, response) {
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

exports.listDevicesInternal = function (model) {
	
	var result = model.find({});
	
	console.log("before result");
	console.log(result);
	console.log("after result");
	return result;
	
	}

exports.deleteDevice = function (model, _mobileNumber, response)
{
console.log('Deleting Device of Mobile Number: ' + _mobileNumber.toString());
model.findOne({MobileNumber: _mobileNumber},
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
console.log('Device not found');
if (response != null) {
response.writeHead(404,
{'Content-Type' : 'text/plain'});
response.end('Device Not Found');
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
	response.send('Device Deleted');
	}
	return;
	}
	}
	});
	}

exports.updateDevice = function (model, requestBody, response) {
	var _mobileNumber = requestBody.MobileNumber;
	model.findOne({MobileNumber: _mobileNumber},
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
	var device = toDevice(requestBody, model);
	if (!data) {
	console.log('Device with Mobile Number: '+ _mobileNumber.toString()
	+ ' does not exist. The Device will be created.');
	device.save(function(error) {
	if (!error)
		device.save();
	});
	if (response != null) {
	response.writeHead(201,
	{'Content-Type' : 'text/plain'});
	response.end('Created');
	}
	return;
	}
	//poulate the document with the updated values
	data.MobileNumber = device.MobileNumber;
	data.DeviceId = device.DeviceId;
	
	
	// now save
	data.save(function (error) {
	if (!error) {
	console.log('Successfully updated Device with MobileNumber: '+ _mobileNumber.toString());
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

exports.findDeviceByDeviceID = function (model, deviceid, response){
	model.find({DeviceId: deviceid}, function(error, result) {
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
