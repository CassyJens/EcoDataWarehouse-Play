package models;
import com.google.code.morphia.annotations.Embedded;

@Embedded
public enum State {
	ACTIVE, DELETED
}