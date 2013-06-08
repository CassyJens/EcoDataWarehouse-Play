package models;

import com.google.code.morphia.annotations.Embedded;
import org.bson.types.ObjectId;
import java.util.Date;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;

@Embedded
public class Standards extends Security.Authenticator {
	
    public ObjectId createdBy;
    public String createdDate = new DT().getDate();
    public ObjectId updatedBy;
    public String updatedDate = new DT().getDate();	
    public State state = State.ACTIVE;

    /**
     * Sets the Standards() createdBy and updatedBy
     * values for the object to which the Standards 
     * object belongs.
     * Status is ACTIVE by default.
     * Created and Updated dates are current date by default.
     */
    public Standards(String email) {
    	try {
	    	User user = User.findByEmail(email);
	    	this.createdBy	= user.id;
	    	this.updatedBy	= user.id;
    	}
    	catch(Exception e) {

    	}
    }

    //http://code.google.com/p/morphia/wiki/EmbeddedAnnotation
}