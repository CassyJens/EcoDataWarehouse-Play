package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.api.templates.Html.*;

import views.html.*;
import models.*;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.bson.types.ObjectId;

/**
 * Manage working group operations.
 */
@Security.Authenticated(Secure.class)
public class WgManagement extends Controller {

    /**
     * Landing page for working group management
     */
    public static Result wgmanagement() {
        return ok(wgmanagement.render("Working Group Management"));
    }

}