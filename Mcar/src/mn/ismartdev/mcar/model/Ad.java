package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Ad {
@DatabaseField(generatedId=true)
public int id;
@DatabaseField
public String title;
@DatabaseField
public int price;
@DatabaseField
public String description;
@DatabaseField
public String phone;
@DatabaseField
public String date;
@DatabaseField
public String category_id;
@DatabaseField
public String images;
@DatabaseField
public int order;

}
