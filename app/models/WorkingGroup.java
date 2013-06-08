package models;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.*;

import org.bson.types.ObjectId;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;

import controllers.MorphiaObject;

@Entity public class WorkingGroup implements ModelApi<WorkingGroup> {

    @Id public ObjectId id;

    @Required public String name;

    @Required public String description;

    public List<ObjectId> fileGroups;

    @Required public Standards standards;

    public WorkingGroup(){}

    /** 
     * Creates a new organization object
     */
    public WorkingGroup(
        String name, 
        String description,
        String email
        ){
    	
        this.name = name;
    	this.description = description;
        this.standards = new Standards(email);
    }

    /**
	 * Returns the WorkingGroup for the provided id, or
	 * throws an exception if the id does not exist.
	 */
	public WorkingGroup findById(ObjectId id) throws Exception {
        WorkingGroup org = MorphiaObject.datastore.find(WorkingGroup.class, "_id", id).get();
        if(null == org) {
            throw new Exception("WorkingGroup " + id.toString() + " not found");
        }
        else return org;
	}

    /**
     * Returns the WorkingGroup for the provided name,
     * or throws an exception if the id doesn't exist.
     */
    public WorkingGroup findByName(String name) throws Exception {
        WorkingGroup org = MorphiaObject.datastore.find(WorkingGroup.class, "name", name).get();
        if(null == org) {
            throw new Exception("WorkingGroup " + name + " not found");
        }
        else return org;
    }

	/**
	 * Inserts an organization.
	 */
	public ObjectId save() {
        MorphiaObject.datastore.save(this);
        return this.id;
    }
}