package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import models.*;

/**
 * Manage projects related operations.
 */
@Security.Authenticated(Secure.class)
public class FileManagement extends Controller {

    public static Result filemanagement() {
        return ok(filemanagement.render("file management"));
    }    


	// TODO BELOW

    /* Invoked when the user wants to get a file? */
    public static Result file(Long id)  {
    	return ok(index.render("file"));
	}

    /* Invoked when the user wants to save a file */
	public static Result createFile() {
		return ok(index.render("createFile"));
	}

}