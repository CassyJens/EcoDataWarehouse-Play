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
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.mongodb.gridfs.GridFSDBFile;
import com.google.code.morphia.Datastore;

import controllers.MorphiaObject;

@Entity public class FileGroup implements ModelApi<FileGroup> {

    @Id public ObjectId id;
	public List<ObjectId> fileGroups;
	public List<ObjectId> fileIds = new ArrayList<ObjectId>();
	public Permission permission; // read write
    public ObjectId owner;
	public Date dateLastModified;
	public Date dateUploaded;
    public String description = "description";
    private Status enumStatus = Status.ACTIVE;	
    @Required
    public String name;
    


    public FileGroup() {}

    public FileGroup(
    	String email, 
    	String name, 
    	String description, 
    	Permission permission) {

    	this.name = name;
    	this.description = description;
    	this.permission = permission;

		try {
            this.owner = User.findByEmail(email).id;
        }
        catch(Exception e) {
            
        }
    }

    /**
     * Creates the default file group for a user
     */
	public FileGroup(String email) throws Exception {
		this.name = "Default for " + email;
		this.permission = Permission.READWRITE; // default
		this.description = "";
        this.owner = User.findByEmail(email).id;
	}

	/**
	 * Adds a file to the file group if it doesn't 
	 * already exist 
	 * Input: ObjectId is the id for the file to add
	 */
	public void addEcoFile(ObjectId id) {

        Query<FileGroup> updateQuery;
		Datastore ds = MorphiaObject.datastore;
		
        try {
            Logger.debug("about to update this biz: " + this.fileIds);

			this.fileIds.add(id);
            this.description = "description";
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
        fg = MorphiaObject.datastore.find(FileGroup.class, "enumStatus", Status.ACTIVE).asList();
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
        Logger.debug("status " + user.status);
        Logger.debug("enumStatus " + user.enumStatus);
        Logger.debug("_id " + user.id);

        FileGroup fg = MorphiaObject.datastore.find(FileGroup.class).field("owner").equal(user.id).get();
        Logger.debug("fg = [" + fg + "]");
        
        if(null == fg) throw new Exception("FileGroup [" + email + "] not found");
        else return fg;

    }

}