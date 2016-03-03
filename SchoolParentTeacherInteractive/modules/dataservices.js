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

function toTeacherRoleList(bodyTeacherRole)
{
	var TeacherRoleList = [];
	var teacherrolecount=0;
	if(bodyTeacherRole !=undefined)
    {
	for(teacherrolecount =0; teacherrolecount <bodyTeacherRole.length; teacherrolecount++)
		{
		    var TeacherRole = {
		    		TeacherRoleType : bodyTeacherRole[teacherrolecount].TeacherRoleType,
		    		TeacherRoleforStd : bodyTeacherRole[teacherrolecount].TeacherRoleforStd,
		    		TeacherRoleforSubjectId : bodyTeacherRole[teacherrolecount].TeacherRoleforSubjectId,
		    		TeacherRoleforSubject : bodyTeacherRole[teacherrolecount].TeacherRoleforSubject,
		    };
		    TeacherRoleList.push(TeacherRole);
		}
    }
	return TeacherRoleList;
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

function toTeacher(body, Teacher)
{
	var teacher = new Teacher({
		TeacherId : body.TeacherId,
		SchoolId: body.SchoolId,
		TeacherFirstName: body.TeacherFirstName,
		TeacherMiddleName: body.TeacherMiddleName,
		TeacherLastName: body.TeacherLastName,
		TeacherDOB : body.TeacherDOB,
		Age : body.Age,
		TeacherGender: body.TeacherGender,
		TeacherFullAddress: body.TeacherFullAddress,
		MobileNumber: body.MobileNumber,
		AlternateMobNumber: body.AlternateMobNumber,
		EmailId: body.EmailId,
		AlternateEmailID: body.AlternateEmailID,
		PresentAddress: body.PresentAddress,
		PresentAddressPOBox: body.PresentAddressPOBox,
		PermanentAddress: body.PermanentAddress,
		PermanentAddressPOBox: body.PermanentAddressPOBox,
		Messages: toMessageList(body.Messages),
		TeacherRoleList : toTeacherRoleList(body.TeacherRoleList)
	});
	
	return teacher;
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


exports.createTeacher = function (model, requestBody, response)
{
	var Teacher = toTeacher(requestBody, model);
	
	Teacher.save(function(err){
		if (err)
			{
			throw err;
			}
		console.log('Teacher saved successfully');
	});
}

exports.findTeacherById = function (model, _teacherId, response) {
	model.findOne({TeacherId: _teacherId},
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
	response.end('Teacher Not Found');
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

exports.listTeachers = function (model, response) {
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

exports.removeTeacher = function (model, _teacherId, response)
{
console.log('Deleting Teacher with Teacher Id: ' + _teacherId);
model.findOne({TeacherId: _teacherId},
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
console.log('Teacher not found');
if (response != null) {
response.writeHead(404,
{'Content-Type' : 'text/plain'});
response.end('Teacher Not Found');
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
	response.send('Deleted Teacher');
	}
	return;
	}
	}
	});
	}

exports.updateTeacher = function (model, requestBody, response) {
	var TeacherId = requestBody.TeacherId;
	model.findOne({TeacherId: TeacherId},
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
	var teacher = toTeacher(requestBody, model);
	if (!data) {
	console.log('Teacher with TeacherID: '+ TeacherId
	+ ' does not exist. The Teacher will be created.');
	teacher.save(function(error) {
	if (!error)
		teacher.save();
	});
	if (response != null) {
	response.writeHead(201,
	{'Content-Type' : 'text/plain'});
	response.end('Created');
	}
	return;
	}
	//poulate the document with the updated values
	data.TeacherId = teacher.TeacherId;
	data.SchoolId = teacher.SchoolId;
	data.TeacherFirstName = teacher.TeacherFirstName;
	data.TeacherMiddleName = teacher.TeacherMiddleName;
	data.TeacherLastName = teacher.TeacherLastName;
	data.TeacherDOB = teacher.TeacherDOB;
	data.Age = teacher.Age;
	data.TeacherGender = teacher.TeacherGender;
	data.TeacherFullAddress = teacher.TeacherFullAddress;
	data.MobileNumber = teacher.MobileNumber;
	data.AlternateMobNumber = teacher.AlternateMobNumber;
	data.EmailId = teacher.EmailId;
	data.AlternateEmailID = teacher.AlternateEmailID;
	data.PresentAddress = teacher.PresentAddress;
	data.PresentAddressPOBox = teacher.PresentAddressPOBox;
	data.PermanentAddress = teacher.PermanentAddress;
	data.PermanentAddressPOBox = teacher.PermanentAddressPOBox;
	data.Messages = teacher.Messages;
	data.TeacherRoleList = teacher.TeacherRoleList;
	
	
	// now save
	data.save(function (error) {
	if (!error) {
	console.log('Successfully updated Teacher with Teacher Id: '+ TeacherId);
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