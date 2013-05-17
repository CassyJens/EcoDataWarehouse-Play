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
import java.util.List;
import java.util.ArrayList;

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

    /**
     * Landing page
     */
    public static Result filemanagement() {
        return ok(filemanagement.render("File Management."));
    }

    /**
     * Presents the form for uploading files.
     */
    public static Result blankUpload(String status){
        return ok(views.html.file.upload.form.render(status));
    }

    /** 
     * Submits the upload form data to the server.
     */
    public static Result submitUpload() {

        String fileName = "";
        String contentType = "";
        String email = "";
        File file;
        EcoFile ecoFile;
        GridFS myFS = MorphiaObject.gridFS;
        GridFSInputFile gridFSFile;
        MultipartFormData body = request().body().asMultipartFormData();
        final List<FilePart> fileList = body.getFiles(); 
        
        for(FilePart fp : fileList) {
            if (fp!= null) {
                try {
                    
                    /* Create the file and save in GridFS */
                    file = fp.getFile();
                    gridFSFile = myFS.createFile(file);
                    gridFSFile.save(); // must be called to close stream
                    Logger.debug(String.format("upload: saved GridFS file id = [%s]", gridFSFile.get("_id").toString()));
                    
                    /* Create symbolic link to store with EcoFile in DB */
                    email = session().get("email");
                    ecoFile = new EcoFile(fp, email, (ObjectId) gridFSFile.get("_id"));
                    ecoFile.save();

                }
                catch(Exception e){
                    return redirect(routes.FileManagement.blankUpload("An unexpected error occured.")); 
                }
            } 
            else {
                flash("error", "Missing file");
                return redirect(routes.FileManagement.blankUpload("The file was unable to be retrieved."));    
            }
        }

        return ok(views.html.file.upload.form.render("File(s) upload success."));
    }

    /**
     * Presents the form for downloading a file
     */
    public static Result blankDownload(String email, String fileGroup) {
        try {
            // By default, render files from the user's default file group (email address)
            List<ObjectId> fileIds = FileGroup.findByEmail(session().get("email")).fileIds;
            List<EcoFile> files = new ArrayList<EcoFile>();
            for(ObjectId id : fileIds) {
                files.add(new EcoFile().findById(id));
            }
            return ok(views.html.file.download.form.render(files));            
        }
        catch(Exception e) {
            return ok(views.html.file.download.form.render(new ArrayList<EcoFile>()));
        }
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
            ecoFile = new EcoFile().findById(id);
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
            return notFound(e.toString() + " The specified file does not exist.");
        }
        catch(Exception e) {
            return notFound("Oh no!! Something went wrong during your file download.");
        }
    }

}