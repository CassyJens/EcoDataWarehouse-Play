package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import views.html.*;
import models.*;

import java.util.*;

/**
 * Renders public access
 */
public class Application extends Controller {
  
    /* The landing page for EcoWarehouse */
    public static Result index() {
        return ok(index.render("Welcome to the UW-La Crosse Eco Warehouse."));
    }  

    /* Returns all active users */
    public static Result findAllUsers() {
    	List<User> users = new User().findAllUsers();
    	return ok(Json.toJson(users));
    }

    /* Returns all active file groups */
    public static Result findAllFileGroups() {
    	List<FileGroup> fgs = new FileGroup().findAllFileGroups();
    	return ok(Json.toJson(fgs));
    }

}
