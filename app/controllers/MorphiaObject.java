package controllers;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;

/**
 * A wrapper for morphia and mongodb objects
 */
public class MorphiaObject {
	static public Mongo mongo;
	static public Morphia morphia;
	static public Datastore datastore;
	static public GridFS gridFS;
}