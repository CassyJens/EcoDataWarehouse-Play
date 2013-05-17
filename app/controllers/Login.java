package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import views.html.login.*;
import models.*;

import javax.validation.*;
import play.data.validation.Constraints.*;

/**
 * Login controller
 * Provides login form, validation, and session
 * management.
 */
public class Login extends Controller {

    public static class LoginForm {
        
        @Required
        @Email
        public String email;

        @Required
        public String password;

        public String validate() {
            
            Logger.debug("in validate");
            Logger.debug("email " + email);
            Logger.debug("password " + password);
            
            if( User.authenticate(email, password) == null ){
                Logger.debug("authenticate = null");
                return "Invalid username or password.";
            }  
            
            Logger.debug("returning null");
            return null;
        } 
    }
      

    /* Invoked when user clicks Log In on the main public nav */
    public static Result blank() {
    	return ok(
            form.render(form(LoginForm.class))
        );
    }

    /**
     * Handle login form submission.
     */
    public static Result submit() {
        Logger.debug("in submit");
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
        
        if(loginForm.hasErrors()) {
            return badRequest(form.render(loginForm));
        } else {
            session("email", loginForm.get().email);
            return redirect(
                routes.FileManagement.filemanagement()
            );
        }
    } 

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Login.blank()
        );
    }     
}