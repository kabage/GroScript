import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVReader;

public class Db {

	static Connection connection = null;
	static Statement db_populate_statement;
	static int bulk_query_size = 2000;

	public static Connection connectToDb(String database_host,
			String database_name, String database_user, String database_pass) {
		/*
		 * OPtimized bulk inserts.
		 */
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			connection = DriverManager
					.getConnection(
							"jdbc:postgresql://"
									+ database_host
									+ ":5432/?useServerPrepStmts=false&rewriteBatchedStatements=true",
							database_user, database_pass);
		} catch (SQLException e) {
			System.out.println("error making connection " + e.toString());
		}

		System.out.println("\n Connected to Database....");

		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("error creating statement " + e.toString());
		}

		String drop_table_fact_data = "DROP TABLE IF EXISTS fact_data";
		try {
			statement.executeUpdate(drop_table_fact_data);
		} catch (SQLException e) {
			System.out.println("unable to drop table" + e.toString());

		}

		String drop_table_stats = "DROP TABLE IF EXISTS stats";
		try {
			statement.executeUpdate(drop_table_stats);
		} catch (SQLException e) {
			System.out.println("unable to drop table" + e.toString());

		}

		String dropDB = "DROP DATABASE " + database_name;

		try {
			statement.executeUpdate(dropDB);
		} catch (SQLException e) {
			System.out.println("unable to drop database " + e.toString());
		}

		String sql = "CREATE DATABASE " + database_name;
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("unable to create database " + database_name
					+ e.toString());
		}
		return connection;
	}

	public static void createDbAndTables(Connection connection) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			System.out.println("error creating statement " + e1.toString());
		}

		String sql = "CREATE TABLE fact_data" + "(id SERIAL not NULL , "
				+ " DOMAIN_DESC VARCHAR(255), "
				+ " COMMODITY_DESC VARCHAR(255), "
				+ " STATISTICCAT_DESC VARCHAR(255), "
				+ " AGG_LEVEL_DESC VARCHAR(255), "
				+ " COUNTRY_NAME VARCHAR(255), " + " STATE_NAME VARCHAR(255), "
				+ " COUNTY_NAME VARCHAR(255), " + " UNIT_DESC VARCHAR(255), "
				+ " VALUE VARCHAR(255), " + " YEAR INTEGER, "
				+ " PRIMARY KEY ( id ))";

		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("error creating table " + e.toString());
		}
		System.out.println(" \n Database table created");
	}

	public static void populateDb(List<Crop> crops) {

		try {
			db_populate_statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("error occured creating db_populate statement "
					+ e.toString());
		}

		for (Crop crop : crops) {
			String query = "insert into fact_data (DOMAIN_DESC, COMMODITY_DESC,STATISTICCAT_DESC,AGG_LEVEL_DESC,COUNTRY_NAME,STATE_NAME,COUNTY_NAME,UNIT_DESC,VALUE,YEAR) values('"
					+ crop.getDOMAIN_DESC()
					+ "','"
					+ crop.getCOMMODITY_DESC()
					+ "','"
					+ crop.getSTATISTICCAT_DESC()
					+ "','"
					+ crop.getAGG_LEVEL_DESC()
					+ "','"
					+ crop.getCOUNTRY_NAME()
					+ "','"
					+ crop.getSTATE_NAME()
					+ "','"
					+ crop.getCOUNTY_NAME()
					+ "','"
					+ crop.getUNIT_DESC()
					+ "','" + crop.getVALUE() + "','" + crop.getYEAR() + "')";

			try {
				db_populate_statement.addBatch(query);
			} catch (SQLException e) {
				System.out.println("Error occurred adding query to batch "
						+ e.toString());
			}

		}
		try {
			db_populate_statement.executeBatch();
		} catch (SQLException e) {
			System.out.println("Error occurred executing batch query "
					+ e.getNextException().toString());
		}
		try {
			db_populate_statement.close();
			// connection.close();
		} catch (SQLException e) {
			System.out
					.println("Error occurred closing db_populate_statement query "
							+ e.toString());
		}

	}

	public static void parseCSVLib(String flat_file, int start, int stop) {

		/* parses csv file extracting required fields line by line */
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(flat_file), '\t');
		} catch (FileNotFoundException e1) {
			System.out.println("file not found exception" + e1.toString());

		}
		List<Crop> crops = new ArrayList<Crop>();

		String[] record = null;

		try {
			reader.readNext();
		} catch (IOException e) {
			System.out.println("error reading file");
		}

		try {

			int count = 0;
			while ((record = reader.readNext()) != null) {

				System.out.print("\r Records read" + ":"
						+ String.valueOf(count++));
				Crop crop = new Crop();
				crop.setAGG_LEVEL_DESC(record[12]);
				crop.setCOMMODITY_DESC(record[3]);
				crop.setCOUNTRY_NAME(record[28]);
				crop.setCOUNTY_NAME(record[21]);
				crop.setDOMAIN_DESC(record[11]);
				crop.setSTATE_NAME(record[16]);
				crop.setSTATISTICCAT_DESC(record[7]);
				crop.setUNIT_DESC(record[8]);
				crop.setYEAR(Integer.valueOf(record[30]));

				crop.setVALUE(record[37]);

				/*
				 * ensure value is validated as an integer before appending to
				 * list
				 */
				if (crops.size() > bulk_query_size) {
					/* sets size of batch queries to be executed */
					populateDb(crops);
					crops.clear();
				} else {
					if (isDataInTimeRange(start, stop, crop.getYEAR())
							&& isLowestRegionCounty(record)) {
						crops.add(crop);

					}
				}
			}
		} catch (IOException e1) {
			System.out.println("Ioexception e1 " + e1.toString());
		}

		populateDb(crops);
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("error closing file");

		}

	}

	public static Boolean isDataInTimeRange(int start, int stop, int YEAR) {
		/* Ensures that the data lies within the required range */
		if (YEAR >= start && YEAR <= stop) {
			return true;
		} else {
			return false;
		}
	}

	public static Boolean isLowestRegionCounty(String[] record) {
		/*
		 * Ensures that the lowest Aggregation level is County.
		 */
		if (record[12].equals("COUNTY")) {
			return true;
		} else {
			return false;
		}
	}

	public static void downloadFile(String url) {
		/* Donwloads the data file from the usda website saving it to /home/ */

		System.out.println("The file is being downloaded.... :-) ");
		File file_location = new File("/home/" + "qs.crops.gz");

		URL file_url = null;
		try {
			file_url = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("URL error " + e.toString());
		}
		try {
			FileUtils.copyURLToFile(file_url, file_location);
		} catch (IOException e) {
			System.out.println("Error donwloading file or saving it "
					+ e.toString());
		}
	}

	public static void saveDataStatistics(Connection connection) {

		try {
			Statement statstatement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* Shows statistics for the state of oregon */
		String filtered_by_value = "select *  INTO stats FROM fact_data  WHERE state_name=OREGON";

	}
}
