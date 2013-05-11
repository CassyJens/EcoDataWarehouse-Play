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


	/* FILE UPLOAD AND DOWNLOAD CONTROLLER ACTIONS */

    /* Presents the form for uploading a file */
    public static Result blankUpload(){
        return ok(views.html.file.upload.form.render());
    }

    /* Presents the upload form to the server */
    public static Result submitUpload() {
        return blankUpload();
    }

    /* Presents the form for downloading a file */
    public static Result blankDownload() {
        return ok(views.html.file.download.form.render());
    }

    /* Passes the download form information to the server */
    public static Result submitDownload() {
        return blankDownload();
    }

}