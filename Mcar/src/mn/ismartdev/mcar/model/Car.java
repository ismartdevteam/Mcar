package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Car {
@DatabaseField(generatedId=true)
public int id;
@DatabaseField
public int car_id;
@DatabaseField
public int seller_id;
@DatabaseField
public String seller_name;
@DatabaseField
public int category_id;
@DatabaseField
public int body_id;
@DatabaseField
public int model_id;
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
public String engine;
@DatabaseField
public int fuel;
@DatabaseField
public int order;
@DatabaseField
public int view_count;
@DatabaseField
public String created_date;
@DatabaseField
public String image_url;
@DatabaseField
public double price;
}
