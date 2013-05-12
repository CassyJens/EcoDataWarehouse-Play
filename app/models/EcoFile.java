package models;

import javax.validation.*;

import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.Date;
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

@Entity public class EcoFile {

    @Id public ObjectId id;             // unique ID
    public String name = "";            // filename
    public List<Object> metadata;       // metadata
    public List<Location> locations;    // list of locations
    public String reference; // reference to original data
    public Date uploadDate;  // upload date
    public User uploadedBy;  // uploaded by
    private int status = 1;  // active, 0 if deleted

    public EcoFile(){}

	public EcoFile(File file){

	}

    /** 
     * Save a mongdb EcoFile object
     */
    public void save(ObjectId _id, String filename) {
        id = _id;
        name = filename;
        MorphiaObject.datastore.save(this);
    }

    /**
     * Returns the inputStream for the file 
     * stored with the given ID.
     * Throws exception if file id does not exist.
     */ 
    public static InputStream retrieve(ObjectId id) throws FileNotFoundException {
        
        GridFSDBFile dbFile = MorphiaObject.gridFS.find(id);
        
        if(null == dbFile) {
            throw new FileNotFoundException("File " + id.toString() + " not found in GridFS");
        }
        else 
            return dbFile.getInputStream();
    }
}