function viewModel() {

	// Load Necessary Lists
	var self = this;
	this.listUsers = ko.observable([]);
	this.listFileGroups = ko.observable([]);

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
		console.log(self.listFileGroups().length);
		self.listFileGroups.valueHasMutated();
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