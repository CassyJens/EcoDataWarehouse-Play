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
@Entity public class EcoFile {

    @Id public ObjectId id;             // unique ID
    public String name = "";            // filename
    public String type = "";            // MIME type
    public List<Object> metadata;       // metadata TODO
    public List<Location> locations;    // list of locations TODO
    public String reference;            // reference to original data
    public String uploadDate;           // string date  
    public User uploadedBy;             // file owner
    private int status = 1;             // active, 0 if deleted

    public EcoFile(){}

	public EcoFile(FilePart fp, String email){
        
        this.name = fp.getFilename();
        Logger.debug(String.format("upload: fileName = [%s]", name));

        this.type = fp.getContentType(); 
        Logger.debug(String.format("upload: contentType = [%s]", type));
        
        try {
            this.uploadedBy = User.getUser(email);
            Logger.debug(String.format("save: email = [%s]", email));
        }
        catch(Exception e) {
            this.uploadedBy = new User("unknown", "unknown", "unknown");
            Logger.debug(String.format("save: email = unknown"));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.uploadDate = dateFormat.format(date);
	}

    /** 
     * Returns the EcoFile document associated with the object id 
     */
    public static EcoFile getEcoFile(ObjectId id) throws Exception {
        EcoFile ef = MorphiaObject.datastore.get(EcoFile.class, id);
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
    public void save(ObjectId id) {
        this.id = id;
        MorphiaObject.datastore.save(this);
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