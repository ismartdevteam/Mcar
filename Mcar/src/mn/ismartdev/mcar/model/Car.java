package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Car {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public int car_id;
	@DatabaseField
	public int seller_id;
	@DatabaseField
	public String seller_name;
	@DatabaseField
	public String model_name;
	@DatabaseField
	public String mark_name;
	@DatabaseField
	public String features;
	@DatabaseField
	public String seller_notes;
	@DatabaseField
	public String door;
	@DatabaseField
	public int category_id;
	@DatabaseField
	public int body_id;
	@DatabaseField
	public int model_id;
	@DatabaseField
	public int mark_id;
	@DatabaseField
	public String modification;
	@DatabaseField
	public String drivetrain;
	@DatabaseField
	public int status;
	@DatabaseField
	public int transmission;
	@DatabaseField
	public int distance;
	@DatabaseField
	public int distance_type;
	@DatabaseField
	public double engine;
	@DatabaseField
	public int fuel;
	@DatabaseField
	public int order;
	@DatabaseField
	public int roller_type;
	@DatabaseField
	public int view_count;
	@DatabaseField
	public String created_date;
	@DatabaseField
	public String image_url;
	@DatabaseField
	public int price;
	@DatabaseField
	public String year;
	@DatabaseField
	public String came_year;
}
