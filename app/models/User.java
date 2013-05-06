package models;

import javax.validation.*;

import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import controllers.MorphiaObject;

@Entity
public class User {

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
        // return find.where()
        //     .eq("email", email)
        //     .eq("password", password)
        //     .findUnique();
        return new User("cassyjens", "jens.cass@gmail.com", "password");
    }

    public static void create(User user) {
        MorphiaObject.datastore.save(user);
    }

}