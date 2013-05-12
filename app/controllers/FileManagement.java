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
 * Manage file operations.
 */
@Security.Authenticated(Secure.class)
public class FileManagement extends Controller {

    public static Result filemanagement() {
        return ok(filemanagement.render("file management"));
    }    

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
        GridFSInputFile gridFSFile;
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart fp = body.getFile("file");

        if (fp!= null) {

            try {

                myFS = MorphiaObject.gridFS;
                file = fp.getFile();
                ecoFile = new EcoFile(fp, session().get("email"));
                gridFSFile = myFS.createFile(file);
                gridFSFile.save(); // must be called to close stream
                
                Logger.debug(String.format("upload: saved file id = [%s]", gridFSFile.get("_id").toString()));
                
                ecoFile.save((ObjectId)gridFSFile.get("_id"));
                return ok(views.html.file.download.summary.render());
            
            }
            catch(Exception e){
            
                return redirect("/filemanagement/upload");
            
            }

        } else {
            flash("error", "Missing file");
            return redirect("/filemanagement/upload");    
        }
    }

    /**
     * Presents the form for downloading a file
     */
    public static Result blankDownload() {
        return ok(views.html.file.download.form.render());
    }

    /**
     * Passes the download form information to the server 
     */
    public static Result submitDownload() {
        return blankDownload();
    }

    /**
     * Returns an okay response with the file download
     */ 
    public static Result streamFile(String objectId){
        
        ObjectId id;
        InputStream is;
        EcoFile ecoFile;

        try {

            // get dependencies 
            id = new ObjectId(objectId);
            ecoFile = EcoFile.getEcoFile(id);
            is = EcoFile.retrieveInputStream(id);

            // set response
            response().setContentType(ecoFile.type);
            response().setHeader(
                "Content-Disposition",
                String.format(
                    "attachment; filename=%s",
                    ecoFile.name
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