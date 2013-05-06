package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import models.*;

public class Application extends Controller {
  
    /* The landing page for EcoWarehouse */
    public static Result index() {
        return ok(index.render("Welcome to the UW-La Crosse Eco Warehouse."));
    }  

}
