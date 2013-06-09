package models;
import com.google.code.morphia.annotations.Embedded;

@Embedded
public enum Permission {
	READ, WRITE, READWRITE, PRIVATE
}