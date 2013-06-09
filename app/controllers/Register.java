package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import views.html.register.*;

import models.*;

/**
 * New User Registration
 */
//@Security.Authenticated(Secure.class)
public class Register extends Controller {

    /**
     *  Defines a FORM wrapping the user class.
     */
    final static Form<User> signupForm = form(User.class);
  
    
    /* VIEWS */


    /**
     * Display a blank form.
     */ 
    public static Result blank() {
        return ok(form.render(signupForm));
    }

    /** 
     *	Validates fields from the registration form
     * 	and either creates a new user or 
     *  communicates any validation errors.
     */
    public static Result submit() {
        Form<User> filledForm = signupForm.bindFromRequest();
        
        // Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Passwords do not match");
            }
        }
        
        // Check if the username and email are valid
        if(!filledForm.hasErrors()) {
            
            String un = filledForm.get().username;
            String email = filledForm.get().email;

            if(un.equals("admin") || un.equals("guest")) {
                filledForm.reject("username", "This username is already taken");
            }
            
            try {
                Logger.debug("Finding user " + email);
                User.findByEmail(email);
                filledForm.reject("email", "There is already an account associated with this email address.");
            }
            catch(Exception e) {
                // continue - the user does not exist
            }
        }
        
        // Return validation results to user or save user
        if(filledForm.hasErrors()) {
            return badRequest(form.render(filledForm));
        } else {
            User user = filledForm.get(); /* create an object from a form */
            User svUser = new User(user.username, user.email, user.password); /* recreate to get save group info */
            svUser.save();            
            return ok(summary.render(svUser));
        }
    }

}
