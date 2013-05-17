package models;
import org.bson.types.ObjectId;

public interface ModelApi<T> {

	/**
	 * ...
	 */
	public T findById(ObjectId id) throws Exception;

	/**
	 * ...
	 */
	public ObjectId save();

}