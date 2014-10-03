package mn.ismartdev.mcar.model;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static String databaseName = "mcar.ormlite";
	private static int databaseVersion = 1;
	Dao<Company, Integer> comDao = null;
	Dao<CompanyType, Integer> comTypeDao = null;
	Dao<Car, Integer> carDao = null;
	Dao<CarBody, Integer> bodyDao = null;
	Dao<CarCategory, Integer> carCatDao = null;
	Dao<CarMark, Integer> markDao = null;
	Dao<CarModel, Integer> modelDao = null;
	Dao<Ad, Integer> adDao = null;
	Dao<AdCat, Integer> adCatDao = null;
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
			TableUtils.createTableIfNotExists(connectionSource, Car.class);
			TableUtils.createTableIfNotExists(connectionSource, CarBody.class);
			TableUtils.createTableIfNotExists(connectionSource,
					CarCategory.class);
			TableUtils.createTableIfNotExists(connectionSource, CarMark.class);
			TableUtils.createTableIfNotExists(connectionSource, CarModel.class);
			TableUtils.createTableIfNotExists(connectionSource, Ad.class);
			TableUtils.createTableIfNotExists(connectionSource, AdCat.class);
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
			TableUtils.createTable(connectionSource, Car.class);
			TableUtils.createTable(connectionSource, CarBody.class);
			TableUtils.createTable(connectionSource, CarCategory.class);
			TableUtils.createTable(connectionSource, CarMark.class);
			TableUtils.createTable(connectionSource, CarModel.class);
			TableUtils.createTable(connectionSource, Ad.class);
			TableUtils.createTable(connectionSource, AdCat.class);
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
	public Dao<Ad, Integer> getAdDao() throws SQLException {
		if (adDao == null)
			adDao = getDao(Ad.class);
		return adDao;
	}

	public Dao<AdCat, Integer> getadCatDao() throws SQLException {
		if (adCatDao == null)
			adCatDao = getDao(AdCat.class);
		return adCatDao;
	}

	public Dao<CompanyType, Integer> getComTypeDao() throws SQLException {
		if (comTypeDao == null)
			comTypeDao = getDao(CompanyType.class);
		return comTypeDao;
	}

	public Dao<Car, Integer> getCarDao() throws SQLException {
		if (carDao == null)
			carDao = getDao(Car.class);
		return carDao;
	}

	public Dao<CarBody, Integer> getBodyDao() throws SQLException {
		if (bodyDao == null)
			bodyDao = getDao(CarBody.class);
		return bodyDao;
	}

	public Dao<CarCategory, Integer> getCarCatDao() throws SQLException {
		if (carCatDao == null)
			carCatDao = getDao(CarCategory.class);
		return carCatDao;
	}

	public Dao<CarMark, Integer> getMarkDao() throws SQLException {
		if (markDao == null)
			markDao = getDao(CarMark.class);
		return markDao;
	}

	public Dao<CarModel, Integer> getModelDao() throws SQLException {
		if (modelDao == null)
			modelDao = getDao(CarModel.class);
		return modelDao;
	}

	// delete all company
	public void deleteCompany() {
		try {
			TableUtils.clearTable(connectionSource, Company.class);
			TableUtils.clearTable(connectionSource, CompanyType.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deleteAd() {
		try {
			TableUtils.clearTable(connectionSource, Ad.class);
			TableUtils.clearTable(connectionSource, AdCat.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deleteCars() {
		try {
			TableUtils.clearTable(connectionSource, Car.class);
			TableUtils.clearTable(connectionSource, CarBody.class);
			TableUtils.clearTable(connectionSource, CarCategory.class);
			TableUtils.clearTable(connectionSource, CarMark.class);
			TableUtils.clearTable(connectionSource, CarModel.class);
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
		carDao = null;
		bodyDao = null;
		carCatDao = null;
		markDao = null;
		modelDao = null;
		super.close();
	}

}
