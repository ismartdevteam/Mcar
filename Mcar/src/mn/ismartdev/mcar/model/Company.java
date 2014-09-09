package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Company {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public int order;
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public String contact;
	@DatabaseField
	public String address;
	@DatabaseField
	public String location;
	@DatabaseField
	public String video;
	@DatabaseField
	
	public String phone;
	@DatabaseField
	public String logo;
	@DatabaseField
	public String images;
	@DatabaseField
	public int type_id;
}
