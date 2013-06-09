function viewModel() {

	// Load Necessary Lists
	var self = this;
	this.listUsers = ko.observable([]);
	this.listFileGroups = ko.observable([]);
	this.listFileGroupsAdd = ko.observable([]);
	this.listWorkingGroups = ko.observable([]);
	this.activeWorkingGroup = ko.observable("");

	// 1. Populate user list
	$.get('/user', function(data) {
		console.log(data);
		for(var i = 0; i < data.length; i++) {
			var user = new User(data[i].email, data[i].id);
			self.listUsers().push(user);
		}
  		self.listUsers.valueHasMutated();
	});

	// 2. Populate file group list
	$.get('/filegroup', function(data) {
		console.log(data);
		for(var i = 0; i < data.length; i++) {
			var fg = new FileGroup(data[i].name, data[i].id);
			self.listFileGroups().push(fg);
		}
		
		/* Always add New File Group Option */
		fg = new FileGroup('New File Group', '-1');
		self.listFileGroups().push(fg);
		self.listFileGroups.valueHasMutated();
	})

	// 3. Populate working groups
	$.get('/workinggroup', function(data) {
		var wg;

		/* Add available working groups for context */
		for(var i = 0; i < data.length; i++) {
			wg = new WorkingGroup(data[i].name, data[i].id);
			self.listWorkingGroups().push(wg);
		}

		/* Always add New Working Group Option */
		wg = new WorkingGroup('New Working Group', '-1');
		self.listWorkingGroups().push(wg);

		self.listWorkingGroups.valueHasMutated();
	})



}



function User(name, id) {
	this.name = name;
	this.id = id;
}

function FileGroup(name, id){
	this.name = name;
	this.id = id;
}

function WorkingGroup(name, id) {
	this.name = name;
	this.id = id;
}