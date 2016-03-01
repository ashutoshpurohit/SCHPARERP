/**
 * http://usejsdoc.org/
 */

function toMessageList(bodyMessage)
{
   var MessageList = [];
   var messagecount = 0;
   if(bodyMessage !=undefined)
	{  
   for(messagecount =0; messagecount < bodyMessage.length ; messagecount++)
	   {
	     var Message = {
	    	 Message: bodyMessage[messagecount].Message,
	    	 Delivered: bodyMessage[messagecount].Delivered
	     };
	     
	     MessageList.push(Message);
	   }
	}
   return MessageList;
}

function toParentList(bodyParent)
{
	var ParentList = [];
	var parentcount=0;
	if(bodyParent !=undefined)
    {
	for(parentcount =0; parentcount <bodyParent.length; parentcount++)
		{
		    var Parent = {
		    	ParentType : bodyParent[parentcount].ParentType,
		    	ParentFirstName : bodyParent[parentcount].ParentFirstName,
		    	ParentLastname : bodyParent[parentcount].ParentLastname,
		    	MobileNumber : bodyParent[parentcount].MobileNumber,
		    	AlternateMobNumber : bodyParent[parentcount].AlternateMobNumber,
		    	EmailId : bodyParent[parentcount].EmailId,
		    	AlternateEmailID : bodyParent[parentcount].AlternateEmailID,
		    	PresentAddress : bodyParent[parentcount].PresentAddress,
		    	PresentAddressPOBox : bodyParent[parentcount].PresentAddressPOBox,
		    	PermanentAddress : bodyParent[parentcount].PermanentAddress,
		    	PermanentAddressPOBox : bodyParent[parentcount].PermanentAddressPOBox,
		    	Messages : toMessageList(bodyParent[parentcount].Messages)
		    	
		    };
		    ParentList.push(Parent);
		}
    }
	return ParentList;
}

function toStudent(body, Student) {
var student =  new Student(
{
	StudentId: body.StudentId,
	SchoolId: body.SchoolId,
	StudentFirstName: body.StudentFirstName,
	StudentMiddleName: body.StudentMiddleName,
	StudentLastName: body.StudentLastName,
	StudentDOB: body.StudentDOB,
	Age: body.Age,
	StudentGender: body.StudentGender,
	StudentClassStandard: body.StudentClassStandard,
	StudentFullAddress: body.StudentFullAddress,
	ParentList : toParentList(body.ParentList)
	
});



return student;
}

exports.createStudent = function (model, requestBody, response)
{
	var Student = toStudent(requestBody, model);	
	Student.save(function(err){
		if (err)
			{
			throw err;
			}
		console.log('Student saved successfully');
	});
}

exports.findStudentById = function (model, _studentId, response) {
		model.findOne({StudentId: _studentId},
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
		response.end('Student Not Found');
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

exports.listStudent = function (model, response) {
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

exports.removeStudent = function (model, _studentId, response)
{
console.log('Deleting Student with Student Id: ' + _studentId);
model.findOne({StudentId: _studentId},
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
console.log('Student not found');
if (response != null) {
response.writeHead(404,
{'Content-Type' : 'text/plain'});
response.end('Student Not Found');
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
	response.send('Deleted Student');
	}
	return;
	}
	}
	});
	}

exports.updateStudent = function (model, requestBody, response) {
	var studentId = requestBody.StudentId;
	model.findOne({StudentId: studentId},
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
	var student = toStudent(requestBody, model);
	if (!data) {
	console.log('Student with StudentID: '+ studentId
	+ ' does not exist. The student will be created.');
	student.save(function(error) {
	if (!error)
		student.save();
	});
	if (response != null) {
	response.writeHead(201,
	{'Content-Type' : 'text/plain'});
	response.end('Created');
	}
	return;
	}
	//poulate the document with the updated values
	data.StudentId = student.StudentId;
	data.SchoolId = student.SchoolId;
	data.StudentFirstName = student.StudentFirstName;
	data.StudentMiddleName = student.StudentMiddleName;
	data.StudentLastName = student.StudentLastName;
	data.StudentDOB = student.StudentDOB;
	data.Age = student.Age;
	data.StudentGender = student.StudentGender;
	data.StudentClassStandard = student.StudentClassStandard;
	data.StudentFullAddress = student.StudentFullAddress;
	data.ParentList = student.ParentList;
	
	// now save
	data.save(function (error) {
	if (!error) {
	console.log('Successfully updated Student with student Id: '+ studentId);
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
