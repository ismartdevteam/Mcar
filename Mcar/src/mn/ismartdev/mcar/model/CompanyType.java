package mn.ismartdev.mcar.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CompanyType {
@DatabaseField(unique=true)
public int id;
@DatabaseField
public String name;
}
