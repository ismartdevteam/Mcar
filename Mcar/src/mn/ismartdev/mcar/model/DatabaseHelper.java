package mn.ismartdev.mcar.model;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static String databaseName = "jor.ormlite";
	private static int databaseVersion = 1;
	Dao<Company, Integer> comDao = null;
	Dao<CompanyType, Integer> comTypeDao = null;

	public DatabaseHelper(Context context) {
		super(context, databaseName, null, databaseVersion);
		// TODO Auto-generated constructor stub
	}

	// creating tables
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			TableUtils.createTableIfNotExists(connectionSource, Company.class);
			TableUtils.createTableIfNotExists(connectionSource,
					CompanyType.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// updgrade tables
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

		try {
			TableUtils.createTable(connectionSource, Company.class);
			TableUtils.createTable(connectionSource, CompanyType.class);
			onCreate(arg0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Dao<Company, Integer> getComDao() throws SQLException {
		if (comDao == null)
			comDao = getDao(Company.class);
		return comDao;
	}

	public Dao<CompanyType, Integer> getComTypeDao() throws SQLException {
		if (comTypeDao == null)
			comTypeDao = getDao(CompanyType.class);
		return comTypeDao;
	}

	// delete all company
	public void deleteUser() {
		try {
			TableUtils.clearTable(connectionSource, Company.class);
			TableUtils.clearTable(connectionSource, CompanyType.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		comDao = null;
		comTypeDao = null;
		super.close();
	}

}
