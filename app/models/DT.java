package models;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DT {

	/**
	 * Helper class for getting current datetime.
	 */

	public String date;

	public DT() {
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = dateFormat.format(new Date());
	}

	public String getDate() {
		return this.date;
	}
}