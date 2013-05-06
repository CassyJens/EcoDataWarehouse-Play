package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import views.html.register.*;

import models.*;

@Security.Authenticated(Secure.class)
public class Register extends Controller {

    /* Defines a form wrapping the user class */
    final static Form<User> signupForm = form(User.class, User.All.class);
  
    /**
     * Display a blank form.
     */ 
    public static Result blank() {
        return ok(form.render(signupForm));
    }

    /* 
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
            if(un.equals("admin") || un.equals("guest")) {
                filledForm.reject("username", "This username is already taken");
            }

            String email = filledForm.get().email;
            if(User.emailExists(email)){
                filledForm.reject("email", "There is already an account associated with this email address.");
            }
        }
        
        if(filledForm.hasErrors()) {
            return badRequest(form.render(filledForm));
        } else {
            User user = filledForm.get();
            User.create(user);
            return ok(summary.render(user));
        }
    }

}
