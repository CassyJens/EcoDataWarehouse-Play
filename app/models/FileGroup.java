package models;

import javax.validation.*;

import play.data.validation.Constraints.*;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.gridfs.GridFSDBFile;
import com.google.code.morphia.Datastore;

import controllers.MorphiaObject;

@Entity public class FileGroup implements ModelApi<FileGroup> {

    /**
     * PROPERTIES
     */
    @Id 
    public ObjectId id;

    @Required
    public String name = "";

    @Required
    public String description = "";

    // File group
	public List<ObjectId> fileGroups = new ArrayList<ObjectId>();
	public List<ObjectId> fileIds = new ArrayList<ObjectId>();
    
    // Metadata
    public List<Object> metadata;
    @Embedded
    public List<Location> locations;

    @Embedded
    public Standards standards;

    /**
     * FUNCTIONS
     */

    public FileGroup() {}

    public FileGroup(
    	String email, 
    	String name, 
    	String description) {

    	this.name = name;
    	this.description = description;
        this.standards = new Standards(email);

		try {
            this.standards.createdBy = User.findByEmail(email).id;
        }
        catch(Exception e) {
            
        }
    }


	/**
	 * Adds a file to the file group if it doesn't 
	 * already exist 
	 * Input: ObjectId is the id for the file to add
	 */
	public void addEcoFile(ObjectId fileId) {

        Query<FileGroup> updateQuery;
		Datastore ds = MorphiaObject.datastore;
		
        try {
            Logger.debug("about to update this biz: " + this.fileIds);

			this.fileIds.add(fileId);
            updateQuery = MorphiaObject.datastore.createQuery(FileGroup.class).field("_id").equal(this.id);
			ds.update(updateQuery, ds.createUpdateOperations(FileGroup.class).set("fileIds", this.fileIds)); 		
		
        }
		catch(Exception e) {
            Logger.debug("An error occurred while updating the file group.");
		}

	}

    /** 
     * Inserts a new file group.
     */
    public ObjectId save() {
        MorphiaObject.datastore.save(this);
        return this.id;
    }

    /** 
     * Returns the file group for the provided id.
     */
    public FileGroup findById(ObjectId id) throws Exception {
        FileGroup fg = MorphiaObject.datastore.find(FileGroup.class, "_id", id).get();
        if(null == fg) {
    		throw new Exception("FileGroup [" + id.toString() + "] not found");
    	} else return fg;
    }

    /**
    * Returns a list of all active filegroups.
    */
    public List<FileGroup> findAllFileGroups() {
        List<FileGroup> fg = new ArrayList<FileGroup>();
        fg = MorphiaObject.datastore.find(FileGroup.class, "standards.state", State.ACTIVE).asList();
        for(FileGroup f : fg) {
            Logger.debug(f.toString());
        }
        return fg;
    }

    /** 
     * Returns the file group for the provided email.
     */
    public static FileGroup findByEmail(String email) throws Exception {

        User user = User.findByEmail(email);

        Logger.debug("username " + user.username);
        Logger.debug("email " + user.email);
        Logger.debug("password " + user.password);
        Logger.debug("_id " + user.id);

        FileGroup fg = MorphiaObject.datastore.find(FileGroup.class).field("owner").equal(user.id).get();
        Logger.debug("fg = [" + fg + "]");
        
        if(null == fg) throw new Exception("FileGroup [" + email + "] not found");
        else return fg;

    }

}