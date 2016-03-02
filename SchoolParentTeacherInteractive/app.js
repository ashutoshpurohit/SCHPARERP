
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , http = require('http')
  , path = require('path')
  , mongoose = require('mongoose')
  , dataservice = require('./modules/dataservices');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}


mongoose.connect('mongodb://localhost/SchoolsDB');

var SchoolSchema = new mongoose.Schema({
	SchoolId: {type: String, required: true, unique: true },
	SchoolFullName: {type: String, required: true},
	SchoolFullAddress: String,
	SchoolMainTelephoneNumber: {type:Number ,required: true},
	AdditionalContactNumbers: 
		[
		 Number
		 ],
	SchoolWebSite: String,
	SchoolCity: String,
	SchoolState: String,
	SchoolAddressPOBox: Number,
	SchoolDistrict: String,
	SchoolType: String	
});

var ParentTypeSchema = new mongoose.Schema({
	ParentType: String
});

var StudentSchema = new mongoose.Schema({
	StudentId: {type: String, required: true, unique: true },
	SchoolId: String,
	StudentFirstName: {type: String, required: true},
	StudentMiddleName: String,
	StudentLastName: {type: String, required: true},
	StudentDOB : Date,
	Age : {type:Number, min:2, max: 24},
	StudentGender: String,
	StudentClassStandard: String,
	StudentFullAddress: String,
	ParentList: [
		{
			ParentType: String,
			ParentFirstName: {type: String, required: true},
			ParentLastname: {type: String, required: true},
			MobileNumber: {type:Number ,required: true},
			AlternateMobNumber: Number,
			EmailId: String,
			AlternateEmailID: String,
			PresentAddress: String,
			PresentAddressPOBox: Number,
			PermanentAddress: String,
			PermanentAddressPOBox: Number,
			Messages:
			[
			 {
			 Message: String,
			 Delivered: Boolean
			 }
			]
			
		}	
	]
});

var TeacherRoleSchema = new mongoose.Schema({
	TeacherRole: String
});

var TeacherSchema = new mongoose.Schema({
	TeacherId: {type: String, required: true, unique: true },
	SchoolId: String,
	TeacherFirstName: {type: String, required: true},
	TeacherMiddleName: String,
	TeacherLastName: {type: String, required: true},
	TeacherDOB : Date,
	Age : Number,
	TeacherGender: String,
	TeacherFullAddress: String,
	MobileNumber: {type:Number ,required: true},
	AlternateMobNumber: Number,
	EmailId: String,
	AlternateEmailID: String,
	PresentAddress: String,
	PresentAddressPOBox: Number,
	PermanentAddress: String,
	PermanentAddressPOBox: Number,
	Messages:
		[
		 {
		 Message: String,
		 Delivered: Boolean
		 }
		],
	TeacherRoleList: [
		{
			TeacherRoleType: String,
			TeacherRoleforStd: String,
			TeacherRoleforSubjectId: String,
			TeacherRoleforSubject: String
		}	
	]
});

var MobileDeviceMappingSchema = new mongoose.Schema({
	MobileNumber: Number,
	DeviceId: String
});

var MessageSchema = new mongoose.Schema({
	From: String,
	To:
		[
		 String
		],
	Message: String,
	DateofMsg: Date,
	DeliveredToAll: Boolean,
	DeliveredSuccessfullyTo:
		[
		 String
		],
	DeliveredFailedTo:
		[
		 String
		]
});

var School = mongoose.model('School', SchoolSchema);

var Student = mongoose.model('Student', StudentSchema);

var ParentType = mongoose.model('ParentType', ParentTypeSchema);

var TeacherType = mongoose.model('TeacherType', TeacherRoleSchema);

var Teacher = mongoose.model('Teacher', TeacherSchema);

var MobileDevice = mongoose.model('MobileDevice', MobileDeviceMappingSchema);

var Message = mongoose.model('Message', MessageSchema);


app.get('/students/:StudentId', function(request, response) {
	console.log(request.url + ' : querying for ' +
	request.params.StudentId);
	dataservice.findStudentById(Student, request.params.StudentId,
	response);
	});

app.post('/students', function(request, response) {
	dataservice.updateStudent(Student, request.body, response)
	});

app.put('/students', function(request, response) {
	dataservice.createStudent(Student, request.body, response)
	});
	
app.del('/students/:StudentId', function(request,response) {
	console.log('request.params.StudentId');
	console.log(request.params.StudentId);
	dataservice.removeStudent(Student, request.params.StudentId, response);
	});
	
app.get('/students', function(request, response) {
		
		console.log('Listing all student with ' + request.params.key +
				'=' + request.params.value);
				dataservice.listStudent(Student, response);
	});
	

app.get('/teachers/:TeacherId', function(request, response) {
	console.log(request.url + ' : querying for ' +
	request.params.TeacherId);
	dataservice.findTeacherById(Teacher, request.params.TeacherId,
	response);
	});

app.post('/teachers', function(request, response) {
	dataservice.updateTeacher(Teacher, request.body, response)
	});

app.put('/teachers', function(request, response) {
	
	dataservice.createTeacher(Teacher, request.body, response)
	});
	
app.del('/teachers/:TeacherId', function(request,response) {
	console.log('request.params.TeacherId');
	console.log(request.params.TeacherId);
	dataservice.removeTeacher(Teacher, request.params.TeacherId, response);
	});
	
app.get('/teachers', function(request, response) {
		
		console.log('Listing all teachers with ' + request.params.key +
				'=' + request.params.value);
				dataservice.listTeachers(Teacher, response);
	});
	

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
