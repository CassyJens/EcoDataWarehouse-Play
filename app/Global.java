import java.net.UnknownHostException;

import play.GlobalSettings;
import play.Logger;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;

import controllers.MorphiaObject;
import models.*;

public class Global extends GlobalSettings {

	@Override
	public void onStart(play.Application arg0) {
		
		super.beforeStart(arg0);
		Logger.debug("** onStart **"); 
		
		try {
			MorphiaObject.mongo = new Mongo("127.0.0.1", 27017);
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		MorphiaObject.morphia = new Morphia();

		MorphiaObject.morphia.map(User.class).map(FileGroup.class).map(EcoFile.class);

		MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "dev");
		MorphiaObject.datastore.ensureIndexes();   
		MorphiaObject.datastore.ensureCaps(); 
		MorphiaObject.gridFS = new GridFS(MorphiaObject.datastore.getDB()); 

		Logger.debug("** Morphia datastore: " + MorphiaObject.datastore.getDB());
	}
}