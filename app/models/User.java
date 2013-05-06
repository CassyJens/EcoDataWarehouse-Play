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

@Entity public class User {

    public interface All {}
    public interface Step1{}    
	public interface Step2{} 

    @Required(groups = {All.class, Step1.class})
    @MinLength(value = 4, groups = {All.class, Step1.class})
    public String username;
    
    @Required(groups = {All.class, Step1.class})
    @Email(groups = {All.class, Step1.class})
    public String email;
    
    @Required(groups = {All.class, Step1.class})
    @MinLength(value = 6, groups = {All.class, Step1.class})
    public String password;

    private int status = 1;

    @Id
    public ObjectId id;

    public User(){}

	public User(String username, String email, String password){
		this.username = username;
		this.email = email;
		this.password = password;
	}

    /**
     * Authenticate a User.
     */
    public static User authenticate(String email, String password) {

        if(MorphiaObject.datastore != null) {
            Query q = MorphiaObject.datastore.createQuery(User.class).filter(
                "email", email).filter(
                "password", password);
            User user = (User) q.get();
            return user;
        }
        return null;
    }

    /** 
     * Insert a user.
     */
    public static void create(User user) {
        MorphiaObject.datastore.save(user);
    }

    /** 
     * Check if email exits in system.
     * Insensitive to status. 
     */
    public static boolean emailExists(String email) {
        User user = MorphiaObject.datastore.find(User.class, "email", email).get();
        if(user != null) return true;
        else return false;
    }

}