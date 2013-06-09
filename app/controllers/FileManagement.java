package controllers;

import play.*;
import play.mvc.*;
import play.templates.*;
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
//@Security.Authenticated(Secure.class)
public class FileManagement extends Controller {

    /**
     * Landing Page. Render the upload file page first.
     */
    public static Result filemanagement() {
        return blankUpload("");
    }

    /**
     * Presents the form for uploading files.
     */
    public static Result blankUpload(String status){
        return ok(filemanagement.render("File Management", views.html.file.upload.form.render(status)));
    }

    /** 
     * Submits the upload form data to the server.
     */
    public static Result submitUpload() {

        String fileName = "";
        String contentType = "";
        String email = "";
        String idString = "";
        File file;
        EcoFile ecoFile;
        GridFS myFS = MorphiaObject.gridFS;
        GridFSInputFile gridFSFile;
        MultipartFormData body = request().body().asMultipartFormData();
        ObjectId id, ecoFileId;
        FileGroup fg;
        final List<FilePart> fileList = body.getFiles(); 
        
        for(FilePart fp : fileList) {
            if (fp!= null) {
                try {
                    
                    /* Create the physical file and save in GridFS */
                    file = fp.getFile();
                    gridFSFile = myFS.createFile(file);
                    gridFSFile.save(); // must be called to close stream
                    Logger.debug(String.format("upload: saved GridFS file id = [%s]", gridFSFile.get("_id").toString()));
                    
                    /* Create symbolic link to store as an EcoFile in DB */
                    email = session().get("email");
                    ecoFile = new EcoFile(fp, email, (ObjectId) gridFSFile.get("_id"));
                    ecoFileId = ecoFile.save();

                    /* Save fileId with selected file group */
                    idString = session().get("fgid");
                    id = new ObjectId(idString);
                    fg = new FileGroup().findById(id);
                    fg.files.add(ecoFileId);
                    fg.save();

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

        return blankUpload("File(s) upload success.");
    }

    /**
     * Presents the form for downloading a file
     */
    public static Result blankDownload() {
        try {

            List<ObjectId> fgIds = new ArrayList<ObjectId>();
            List<ObjectId> fIds = new ArrayList<ObjectId>();
            List<EcoFile> files = new ArrayList<EcoFile>();
            String email = session().get("email");
            List<ObjectId> wgIds = new User().findByEmail(email).workingGroups;

            /* for the first working group */
            for(ObjectId id : wgIds) {
            
                fgIds = new WorkingGroup().findById(id).fileGroups;
                Logger.debug("Number of file groups in working group: " + fgIds.size());
                
                /* for the file group that matches the session */
                for(ObjectId fgId : fgIds) {
                    
                    Logger.debug("session id " + session().get("fgid"));
                    Logger.debug("file group id " + fgId.toString());
                    
                    if(fgId.toString().equals(session().get("fgid"))) {

                        /* add all of the files to the file list */
                        fIds = new FileGroup().findById(fgId).files;

                        for(ObjectId fid : fIds) {

                            files.add(new EcoFile().findById(fid));

                        }

                        Logger.debug("Number of files in file group: " + files.size());

                    }
                
                }
            
                break;
            
            }

            for(EcoFile file : files) {
                Logger.debug("File: " + file.toString());
            }

            return ok(filemanagement.render("File Management", views.html.file.download.form.render(files)));   
        }
        catch(Exception e) {
            return ok(filemanagement.render("File Management", views.html.file.download.form.render(new ArrayList<EcoFile>())));
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