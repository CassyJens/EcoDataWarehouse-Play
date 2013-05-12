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

@Entity public class FileGroup {

    @Id public ObjectId id;
	List<FileGroup> fileGroups;
	List<EcoFile> files;
	List<Permission> permissions; // read write
	User owner;
	Date lastModified;
	Date dateUploaded;
	String description;
	String name;

    private int status = 1;

    public FileGroup(){}

	public FileGroup(String name){
		this.name = name;
	}

    /** 
     * Insert a user.
     */
    public static void create(FileGroup FileGroup) {
        MorphiaObject.datastore.save(FileGroup);
    }

}