package models;
import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.*;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;

import controllers.MorphiaObject;
import org.bson.types.ObjectId;

@Entity public class User implements ModelApi<User> {

    @Required 
    @MinLength(value = 4) 
    public String username;  
    
    @Required 
    @Email 
    public String email;
    
    @Required 
    @MinLength(value = 6) 
    public String password;
    
    public List<ObjectId> workingGroups; 
    @Embedded public Standards standards;
    @Id public ObjectId id;

    public User(){}

	public User(
        String username, 
        String email, 
        String password){

		this.username = username;
		this.email = email;
		this.password = password;
	    this.standards = new Standards(email);
        this.workingGroups.add(new WorkingGroup(email, "User's Default WorkingGroup", email).save());
    }

    /**
     * Authenticate a User.
     */
    public static User authenticate(String email, String password) {

        Logger.debug("email: " + email);
        Logger.debug("password: " + password);

        if( MorphiaObject.datastore != null) {

            Logger.debug("datastore not null");

            Query q = MorphiaObject.datastore.createQuery(User.class).filter(
                "email", email).filter(
                "password", password);

            Logger.debug("query created");
            
            User user = (User) q.get();
            
            Logger.debug("user is retrieved");
            return user;
        }
        return null;
    }

    /** 
     * Insert a user.
     */
    public ObjectId save() {
        // if exists, update
        // TODO
        // else, save new 
        MorphiaObject.datastore.save(this); // @id is automatically filled in after save
        return this.id;
    }

    /**
     * Returns all active users in the system.
     */
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<User>();
        users = MorphiaObject.datastore.find(User.class, "standards.state", State.ACTIVE).asList();
        return users;
    }

    /**
     * Retrieves a user from the database.
     */
    public User findById(ObjectId id) throws Exception {
        User user = MorphiaObject.datastore.find(User.class, "_id", id).get();
        if(null == user) {
            throw new Exception("User " + id.toString() + " not found");
        }
        else return user;
    }

    /**
     * Returns a user given an email.
     */
    public static User findByEmail(String email) throws Exception {
        User user = MorphiaObject.datastore.find(User.class, "email", email).get();
        if(user != null) {
            Logger.debug("User found: " + user.email);
            return user;
        }
        else throw new Exception("User " + email + " does not exist");
    }

}