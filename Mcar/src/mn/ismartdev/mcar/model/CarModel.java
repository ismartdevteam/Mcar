package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CarModel {
	@DatabaseField(unique=true)
	public int id;
	@DatabaseField
	public int mark_id;
	@DatabaseField
	public String name;
}
