package models;

import javax.validation.*;

import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;

import controllers.MorphiaObject;

@Entity public class File {

    @Id public ObjectId id;             // unique ID
    public String name = "";            // filename
    public List<Object> metadata;       // metadata
    //public List<Location> locations;    // list of locations
    public String reference; // reference to original data
    public Date uploadDate;  // upload date
    public User uploadedBy;  // uploaded by
    private int status = 1;  // active, 0 if deleted

    public File(){}

	public File(String name){
		this.name = name;
	}

    /** 
     * Insert a user.
     */
    public static void create(File file) {
        MorphiaObject.datastore.save(file);
    }

}