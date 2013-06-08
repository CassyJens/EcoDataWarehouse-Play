package models;

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
import javax.validation.*;

import com.mongodb.gridfs.GridFSDBFile;
import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;

import controllers.MorphiaObject;

/**
 * EcoFile: A wrapper for a file. Stores file metadata
 * and a reference to the file in MongoDB.
 */
@Entity public class EcoFile implements ModelApi<EcoFile> {

    /**
     * PROPERTIES
     */

    @Id public ObjectId id;             // unique ID
    
    @Required public String name = "";  // filename
    public String description = "";     // file description (optional)

    // File 
    public String type = "";            // MIME type
    public String reference;            // reference to original data

    // Metadata
    public List<Object> metadata;       // metadata TODO
    
    @Embedded public List<Location> locations;    // list of locations TODO
    @Embedded public Standards standards;  // insert/update metadata

    /**
     * FUNCTIONS
     */

    public EcoFile(){}

	public EcoFile(
        FilePart fp, 
        String email, 
        ObjectId id){
        
        this.id = id;
        this.name = fp.getFilename();
        this.type = fp.getContentType(); 
        this.standards = new Standards(email);

        Logger.debug(String.format("upload: fileName = [%s]", name));
        Logger.debug(String.format("upload: contentType = [%s]", type));
        Logger.debug(String.format("upload: email = [%s]", email));
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

    /** 
     * Insert an EcoFile.
     */
    public ObjectId save() {
        // if exists, update
        // TODO
        // else, save new 
        MorphiaObject.datastore.save(this); // @id is automatically filled in after save
        return this.id;
    }
      
}