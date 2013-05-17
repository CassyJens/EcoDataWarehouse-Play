package models;

import javax.validation.*;
import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;
import play.mvc.Http.MultipartFormData.FilePart;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.gridfs.GridFSDBFile;

import controllers.MorphiaObject;

/**
 * Represents one file
 */
@Entity public class EcoFile implements ModelApi<EcoFile> {

    @Id public ObjectId id;             // unique ID
    public String name = "";            // filename
    public String type = "";            // MIME type
    public List<Object> metadata;       // metadata TODO
    public List<ObjectId> locations;    // list of locations TODO
    public String reference;            // reference to original data
    public String uploadDate;           // string date  
    public ObjectId owner;             // file owner
    public Status enumStatus = Status.ACTIVE;

    public EcoFile(){}

	public EcoFile(FilePart fp, String email, ObjectId id){
        
        this.id = id;
        
        this.name = fp.getFilename();
        Logger.debug(String.format("upload: fileName = [%s]", name));

        this.type = fp.getContentType(); 
        Logger.debug(String.format("upload: contentType = [%s]", type));
        
        try {
            this.owner = User.findByEmail(email).id;
            Logger.debug(String.format("upload: email = [%s]", email));
        }
        catch(Exception e) {

        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.uploadDate = dateFormat.format(date);

        Logger.debug("eco file created");
	}

    /** 
     * Returns the EcoFile document associated with the object id 
     */
    public EcoFile findById(ObjectId id) throws Exception {
        EcoFile ef = MorphiaObject.datastore.find(EcoFile.class, "_id", id).get();
        if(null == ef) {
            throw new Exception("EcoFile " + id.toString() + " not found in Mongo datastore");
        }
        else return ef; 
    }


    /**
     * Returns the GridFSDbFile for the object id 
     */
    public static GridFSDBFile getDBFile(ObjectId id) throws FileNotFoundException {
        GridFSDBFile dbFile = MorphiaObject.gridFS.find(id);
        if(null == dbFile) {
            throw new FileNotFoundException("File " + id.toString() + " not found in GridFS");
        }
        else
            return dbFile;
    }    

    /** 
     * Save a mongdb EcoFile object
     */
    public ObjectId save() {

        /**
         * Create EcoFile in the db
         */ 
        MorphiaObject.datastore.save(this);

        /**
         * Adds the EcoFile to the default 
         * user file group.
         */

        FileGroup fg;
        String s = "";
        
        try {
            s = new User().findById(this.owner).email;
            fg = FileGroup.findByEmail(s);
        }
        catch(Exception e) {
            
            Logger.debug("**********file group by email " + s + "not found");
            
            try {
                fg = new FileGroup(s);
                fg.save();
            }
            catch(Exception e2) {
                return null;
            }

        }
        
        fg.addEcoFile(this.id);

        return this.id;
    }

    /**
     * The file is saved with the default file group 
     * unless the file group is specified here and this
     * save method is used.
     */
    public ObjectId saveWithFileGroup(ObjectId fgId) throws Exception {

        MorphiaObject.datastore.save(this);
        ObjectId fgID;
        FileGroup fg;
        
        /**
         * Adds the EcoFile to the default 
         * user file group.
         */

        fg = new FileGroup().findById(fgId);
        fg.addEcoFile(this.id);

        return this.id;
    }

    /**
     * Returns the inputStream for the file 
     * stored with the given ID.
     * Throws exception if file id does not exist.
     */ 
    public static InputStream retrieveInputStream(ObjectId id) throws FileNotFoundException {
        try {
            return getDBFile(id).getInputStream();       
        }
        catch(FileNotFoundException e){
            throw e;
        }
    }    
}