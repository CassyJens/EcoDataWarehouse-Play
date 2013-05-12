package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import views.html.*;
import models.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.mongodb.Mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.bson.types.ObjectId;

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

    /* Submits the upload form data to the server */
    public static Result submitUpload() {

        String fileName = "";
        String contentType = "";
        File file;
        EcoFile ecoFile;
        GridFS myFS;
        GridFSInputFile saveMe;
        
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart fp = body.getFile("file");
      
        // TODO get the MIME type

      if (fp!= null) {

        fileName = fp.getFilename();
        Logger.debug(String.format("upload: fileName = [%s]", fileName));

        contentType = fp.getContentType(); 
        Logger.debug(String.format("upload: contentType = [%s]", contentType));

        file = fp.getFile();
        ecoFile = new EcoFile(file);

        // returns default GridFS bucket (i.e. "fs" collection)
        myFS = MorphiaObject.gridFS;

        try {
            // saves the file to "fs" GridFS bucket
            saveMe = myFS.createFile(file);
        }
        catch(Exception e){
            return redirect("/filemanagement/upload");
        }

        saveMe.save(); // must be called to close stream
        Logger.debug(String.format("upload: saved file id = [%s]", saveMe.get("_id").toString()));

        // Store a refere
        ecoFile.save((ObjectId)saveMe.get("_id"), fileName);

        return ok(views.html.file.download.summary.render());

      } else {
        flash("error", "Missing file");
        return redirect("/filemanagement/upload");    
      }
    }

    /* Presents the form for downloading a file */
    public static Result blankDownload() {
        return ok(views.html.file.download.form.render());
    }

    /* Passes the download form information to the server */
    public static Result submitDownload() {
        return blankDownload();
    }

    public static Result streamFile(String objectId){
        
        try {
            ObjectId id = new ObjectId(objectId);
            InputStream is = EcoFile.retrieve(id);
            response().setContentType("application/json");
            response().setHeader(
                "Content-Disposition",
                String.format(
                    "attachment; filename=%s",
                    "your-properly-named-file.json" // TODO: this better
                )
            );
            return ok(is);
        }
        catch(FileNotFoundException e){
            return notFound("The specified file does not exist.");
        }
        catch(Exception e) {
            return notFound("Oh no!! Something went wrong during your file download.");
        }
    }

}