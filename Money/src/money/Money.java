
/*
 * to-do:
 * check for suspected dup credit card transactions and report to console as "possible"
 * sort credit card uncategorised logic. maybe just record cc trans as type "CC" and categorise as "credit card trans"
 * should i categorise transfer between accounts as "transfer/to paul" etc?
 */

package money;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Date;

public class Money
{
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, ParseException
	{
		// Load money.properties 
		Properties prop = loadPropertiesFile();
		
		// Load data from text files 
		List<List<String>> contentOfAllRbsDataFiles = loadRbsDataFiles(prop.getProperty("rbsdatafilepath"));
		List<List<String>> contentOfAllBosDataFiles = loadBosDataFiles(prop.getProperty("bosdatafilepath"));
		List<List<String>> contentOfAllBos2DataFiles = loadBos2DataFiles(prop.getProperty("bos2datafilepath"));
		List<List<String>> contentOfAllBosCcDataFiles = loadBosCcDataFiles(prop.getProperty("bosccdatafilepath"));
		List<List<String>> contentOfAllAmexDataFiles = loadAmexDataFiles(prop.getProperty("amexdatafilepath"));
		List<List<String>> contentOfAllYbDataFiles = loadYbDataFiles(prop.getProperty("ybdatafilepath"));
		List<List<String>> contentOfAllOtherDataFiles = loadOtherDataFiles(prop.getProperty("otherdatafilepath"));
		List<List<String>> contentOfCategoriesFile = loadCategoriesTextFile(prop.getProperty("categoriesfile"));
		List<List<String>> contentOfIgnoreTransFile = loadIgnoreTransTextFile(prop.getProperty("ignoretransfile"));
		List<List<String>> contentOfAccountsFile = loadAccountsTextFile(prop.getProperty("accountsfile"));
		
		// Delete database
		String pDatabase = prop.getProperty("database");
		deleteDatabase(new File(pDatabase));
		
		// Create database connection
		Connection dbConnection = createDatabaseConnection();
		
		// Create database objects
		createDdl(dbConnection);
		
		// Populate tables with base data
		populateTablesWithBaseData(dbConnection,
								   contentOfAllRbsDataFiles,
								   contentOfAllBosDataFiles,
								   contentOfAllBos2DataFiles,
								   contentOfAllBosCcDataFiles,
								   contentOfAllAmexDataFiles,
								   contentOfAllYbDataFiles,
								   contentOfAllOtherDataFiles,
								   contentOfCategoriesFile, 
								   contentOfIgnoreTransFile,
								   contentOfAccountsFile);

		// Transform data
		transformData(dbConnection);
		
		// Create database objects that are dependent on transaction data being loaded first
		createDdl2(dbConnection);

		// Create text file of potential transactions to be ignored (ie. uncategorised transactions)
		writeTextFileIgnoreTrans(dbConnection, prop.getProperty("uncategorisedfile"));
	}

	static Properties loadPropertiesFile()
	{
		Properties prop = new Properties();
		InputStream input = MethodHandles.lookup().lookupClass().getResourceAsStream("money.properties");

		try
		{
			// Load money.properties file
			prop.load(input);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try 
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}

	// Load RBS transaction data text files
	static List<List<String>> loadRbsDataFiles(String pRbsDataFilePath)
	{
		File dataFilePath = new File(pRbsDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}

	// Load BOS transaction data text files
	static List<List<String>> loadBosDataFiles(String pBosDataFilePath)
	{
		File dataFilePath = new File(pBosDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}

	// Load BOS2 transaction data text files (new format they started producing)
	static List<List<String>> loadBos2DataFiles(String pBos2DataFilePath)
	{
		File dataFilePath = new File(pBos2DataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}

	// Load BOS Credit Card transaction data text files
	static List<List<String>> loadBosCcDataFiles(String pBosCcDataFilePath)
	{
		File dataFilePath = new File(pBosCcDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}
	
	// Load Amex Card transaction data text files
	static List<List<String>> loadAmexDataFiles(String pAmexDataFilePath)
	{
		File dataFilePath = new File(pAmexDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}
	
	// Load Yorkshire Bank Card transaction data text files
	static List<List<String>> loadYbDataFiles(String pYbDataFilePath)
	{
		File dataFilePath = new File(pYbDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}
	
	// Load balances of other accounts
	static List<List<String>> loadOtherDataFiles(String pOtherDataFilePath)
	{
		File dataFilePath = new File(pOtherDataFilePath);
		String[] listOfDataFiles = dataFilePath.list();
		List<String> contentOfAllDataFiles = new ArrayList<>();
		
		// Load transaction data files one at a time
		for (int i=0; i < listOfDataFiles.length; i++)
		{
			String filename = dataFilePath.getPath() + "\\" + listOfDataFiles[i];
			
			try
			{
				contentOfAllDataFiles.addAll(loadTextFile(filename, i));
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
				
		List<List<String>> contentOfAllDataFiles2D = convertTo2D(contentOfAllDataFiles);
		
		return contentOfAllDataFiles2D;
	}

	// Load categories text file
	static List<List<String>> loadCategoriesTextFile(String pCategoriesFile)
	{
		List<String> contentOfCategoriesFile = new ArrayList<>();
		
		String filename = pCategoriesFile;
				
		try
		{
			contentOfCategoriesFile.addAll(loadTextFile(filename, 1));
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
				
		List<List<String>> contentOfCategoriesFile2D = convertTo2D(contentOfCategoriesFile);
		
		return contentOfCategoriesFile2D;
	}
	
	// Load ignore transactions text file
	static List<List<String>> loadIgnoreTransTextFile(String pIgnoreTransFile)
	{
		List<String> contentOfIgnoreTransFile = new ArrayList<>();
		
		String filename = pIgnoreTransFile;
				
		try
		{
			contentOfIgnoreTransFile.addAll(loadTextFile(filename, 1));
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
				
		List<List<String>> contentOfIgnoreTransFile2D = convertTo2D(contentOfIgnoreTransFile);
		
		return contentOfIgnoreTransFile2D;
	}
	
	// Load accounts text file
	static List<List<String>> loadAccountsTextFile(String pAccountsFile)
	{
		List<String> contentOfAccountsFile = new ArrayList<>();
		
		String filename = pAccountsFile;
				
		try
		{
			contentOfAccountsFile.addAll(loadTextFile(filename, 1));
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
				
		List<List<String>> contentOfAccountsFile2D = convertTo2D(contentOfAccountsFile);
		
		return contentOfAccountsFile2D;
	}
	
	static List<String> loadTextFile(String path, int fileid) throws IOException
	{
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		List<String> textFileContents = new ArrayList<>();
		String fileLine;
		String lineWithIds;
		int lineInFileId = 0;
		
		while ((fileLine = br.readLine()) != null)
		{
			lineWithIds = fileid + "," + lineInFileId++ + "," + fileLine;
			textFileContents.add(lineWithIds);
		}
		
		br.close();
		return textFileContents;
	}

	static void writeTextFileIgnoreTrans(Connection dbConnection, String pUncategorisedFile) throws IOException, SQLException
	{
		String filename = pUncategorisedFile;
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bw = new BufferedWriter(fw);
		Statement statement = null;
		statement = dbConnection.createStatement();
		
		String query = "SELECT * FROM uncategorised_view";
		
        ResultSet rs = statement.executeQuery(query);
        
    	System.out.println("Uncategorised:");
        
        while (rs.next())
        {
        	System.err.println(rs.getDate("DATE") + "," + 
       			 			   rs.getString("TYPE") + ",\"" + 
				       		   rs.getString("DESCRIPTION") + "\"," + 
				       		   rs.getBigDecimal("VALUE") + "," +
				       		   rs.getBigDecimal("BALANCE"));
        	
        	bw.write(rs.getDate("DATE") + "," + 
        			 rs.getString("TYPE") + ",\"" + 
        			 rs.getString("DESCRIPTION") + "\"," + 
        			 rs.getBigDecimal("VALUE") + "," +
        			 rs.getBigDecimal("BALANCE"));
        	
        	bw.newLine();
        }	
        				
		bw.close();
	}
	
	static List<List<String>> convertTo2D(List<String> allLines)
	{
		List<List<String>> allLines2D = new ArrayList<>(new ArrayList<>());

		for (int i=0; i < allLines.size(); i++)
		{
			List<String> lineSplit = splitCsvLine(allLines.get(i));
			allLines2D.add(lineSplit);
		}
		return allLines2D;
	}
	
	static List<String> splitCsvLine(String csvLine)
	{
		List<String> result = new ArrayList<String>();
		int start = 0;
		boolean inQuotes = false;

		for (int currentPos = 0; currentPos < csvLine.length(); currentPos++)
		{
		    if (csvLine.charAt(currentPos) == '\"') inQuotes = !inQuotes;
		    boolean atLastChar = (currentPos == csvLine.length() - 1);
		    if (atLastChar)
		    {
		    	if (csvLine.charAt(currentPos) == ',')
		    	{
		    		result.add(csvLine.substring(start,currentPos).replaceAll("\"|\'",""));
		    	}
		    	else
		    	{
		    		result.add(csvLine.substring(start).replaceAll("\"|\'",""));
		    	}
		    }
		    else if (csvLine.charAt(currentPos) == ',' && !inQuotes)
		    {
		    	result.add(csvLine.substring(start, currentPos).replaceAll("\"|\'",""));
		    	start = currentPos + 1;
		    }
		}
		
		for (int i = result.size(); i <= 9; i++)
		{
			result.add("");
		}
		
		return result;
	}
	
	/* 
	The method creates a Connection object. Loads the embedded driver,
	starts and connects to the database using the connection URL.
	*/
	static Connection createDatabaseConnection() throws SQLException, ClassNotFoundException
	{
	    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	    Class.forName(driver);
	    String url = "jdbc:derby:db/moneyDB;create=true";
	    Connection c = DriverManager.getConnection(url);
	    return c;
	}
	
	static boolean createDdl(Connection dbConnection)
	{
	    boolean bCreatedTables = false;
	    Statement statement = null;
	    try
	    {
	        statement = dbConnection.createStatement();
	        String strDdlCommands[] = setDdlCommands();
	        for (int i=0; i < strDdlCommands.length; i++)
	        {
	            statement.execute(strDdlCommands[i]);
	        }
	        bCreatedTables = true;
	    }
	    catch (SQLException ex)
	    {
	        ex.printStackTrace();
	    }
	    
	    return bCreatedTables;
	}
	
	static boolean createDdl2(Connection dbConnection)
	{
	    boolean bCreatedTables = false;
	    Statement statement = null;
	    try
	    {
	        statement = dbConnection.createStatement();
	        String strDdlCommands[] = setDdlCommands2();
	        for (int i=0; i < strDdlCommands.length; i++)
	        {
	            statement.execute(strDdlCommands[i]);
	        }
	        bCreatedTables = true;
	    }
	    catch (SQLException ex)
	    {
	        ex.printStackTrace();
	    }
	    
	    return bCreatedTables;
	}
	
	static String[] setDdlCommands()
	{
		String[] strDdlCommands = new String[10];

		strDdlCommands[0] = "CREATE TABLE text_files_varchar"
				  		  + "("
				  		  + "file_id VARCHAR(15),"
					      + "line_id VARCHAR(15),"
				  		  + "account_id VARCHAR(3),"
						  + "date VARCHAR(30),"
						  + "type VARCHAR(30),"
						  +	"description VARCHAR(150),"
						  +	"value VARCHAR(15),"
						  +	"balance VARCHAR(15),"
						  +	"account_name VARCHAR(30),"
						  +	"account_number VARCHAR(20)"
						  +	")";

		strDdlCommands[1] = "CREATE TABLE text_files_type"
						  + "("
						  + "file_id INTEGER NOT NULL,"
					      + "line_id INTEGER NOT NULL,"
						  + "account_id INTEGER,"
					      + "date DATE,"
						  + "type VARCHAR(3),"
						  +	"description VARCHAR(150),"
						  +	"value DECIMAL(10,2),"
						  +	"balance DECIMAL(10,2),"
						  +	"account_name VARCHAR(30),"
						  +	"account_number VARCHAR(20)"
						  +	")";

		strDdlCommands[2] = "CREATE TABLE all_transactions"
						  + "("
						  + "transaction_id INTEGER NOT NULL PRIMARY KEY"
						  + "			    GENERATED ALWAYS AS IDENTITY"
						  + "			    (START WITH 1, INCREMENT BY 1),"
						  + "file_id INTEGER NOT NULL,"
					      + "line_id INTEGER NOT NULL,"
						  + "account_id INTEGER, "
						  + "date DATE,"
						  + "type VARCHAR(3),"
						  +	"description VARCHAR(150),"
						  +	"value DECIMAL(10,2),"
						  +	"balance DECIMAL(10,2),"
						  + "category_id INTEGER"
						  +	")";

		strDdlCommands[3] = "CREATE TABLE categories"
						  + "("
						  + "category_id INTEGER NOT NULL PRIMARY KEY"
						  + "			 GENERATED ALWAYS AS IDENTITY"
						  + "			 (START WITH 1, INCREMENT BY 1),"
						  + "category_name VARCHAR(30),"
						  + "sub_category_name VARCHAR(30),"
						  + "sort_order INTEGER"
						  +	")";

		strDdlCommands[4] = "CREATE TABLE category_map"
						  + "("
						  + "category_map_id INTEGER NOT NULL PRIMARY KEY"
						  + "				 GENERATED ALWAYS AS IDENTITY"
						  + "				 (START WITH 1, INCREMENT BY 1),"
						  + "category_id INTEGER NOT NULL,"
						  +	"pattern_match VARCHAR(150),"
						  + "trans_type VARCHAR(3)"
						  +	")";

		strDdlCommands[5] = "CREATE TABLE accounts"
				  		  + "("
				  		  + "account_id INTEGER NOT NULL PRIMARY KEY,"
				  		  + "bank VARCHAR(15),"
				  		  + "sort_code VARCHAR(15),"
				  		  + "account_number VARCHAR(30),"
				  		  + "account_type VARCHAR(15),"
				  		  + "account_name VARCHAR(30),"
				  		  + "customer_number VARCHAR(15)"
				  		  +	")";

		strDdlCommands[6] = "CREATE VIEW category_map_view "
						  + "AS SELECT "
						  + "c.category_id, "
						  + "c.category_name, "
						  + "c.sub_category_name, "
						  + "cm.pattern_match, "
						  + "cm.trans_type "
						  + "FROM categories c, category_map cm "
						  + "WHERE cm.category_id = c.category_id";

		strDdlCommands[7] = "CREATE VIEW all_transactions_view "
				  		  + "AS SELECT "
				  		  + "atr.*, acc.bank, acc.sort_code, acc.account_number, acc.account_type, acc.account_name, acc.customer_number, c.category_name, c.sub_category_name, c.sort_order, "
				  		  + "YEAR(atr.date) CAL_YEAR, "
				  		  + "MONTH(atr.date) CAL_MONTH, "
				  		  + "YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) RBS_YEAR, "
				  		  + "MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) RBS_MONTH, "
				  		  + "MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,CURRENT_TIMESTAMP)}) - MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) + (YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,CURRENT_TIMESTAMP)}) - YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)})) * 12 RBS_MONTHS_AGO, "
				  		  + "CASE "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 1 THEN 'Jan' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 2 THEN 'Feb' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 3 THEN 'Mar' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 4 THEN 'Apr' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 5 THEN 'May' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 6 THEN 'Jun' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 7 THEN 'Jul' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 8 THEN 'Aug' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 9 THEN 'Sep' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 10 THEN 'Oct' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 11 THEN 'Nov' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "	WHEN MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) = 12 THEN 'Dec' || ' ' || CAST(YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) as CHAR(4)) "
				  		  + "END rbs_month_name "
						  + "FROM all_transactions atr LEFT OUTER JOIN categories c "
				  		  + "ON c.category_id = atr.category_id "
						  + "JOIN accounts acc ON acc.account_id = atr.account_id";

		strDdlCommands[8] = "CREATE TABLE ignore_transactions"
						  + "("
						  + "date DATE,"
						  + "type VARCHAR(3),"
						  +	"description VARCHAR(150),"
						  +	"value DECIMAL(10,2),"
						  +	"balance DECIMAL(10,2),"
						  +	"account_name VARCHAR(30),"
						  +	"account_number VARCHAR(20)"
						  +	")";

		strDdlCommands[9] = "CREATE VIEW uncategorised_view "
						  + "AS SELECT * "
						  + "FROM all_transactions atr "
						  + "WHERE category_id is null "
						  + "AND NOT EXISTS ("
						  + "SELECT 1 from ignore_transactions itr "
						  + "WHERE itr.date = atr.date "
						  + "AND itr.type = atr.type "
						  + "AND itr.description = atr.description "
						  + "AND itr.value = atr.value "
						  + "AND itr.balance = atr.balance)";

		return strDdlCommands;
	}
	
	static String[] setDdlCommands2()
	{
		String[] strDdlCommands = new String[3];
		
		strDdlCommands[0] = "CREATE TABLE monthly_balance_transactions "
				  		  + "AS SELECT account_id, rbs_year, rbs_month, transaction_id "
				  		  + "FROM all_transactions_view "
				  		  + "WITH NO DATA";

		strDdlCommands[1] = "INSERT INTO monthly_balance_transactions "
				  		  + "SELECT account_id, rbs_year, rbs_month, MAX(transaction_id) transaction_id "
				  		  + "FROM all_transactions_view "
				  		  + "GROUP BY account_id, rbs_year, rbs_month";

		strDdlCommands[2] = "CREATE VIEW monthly_balances_view "
						  + "AS SELECT atv.account_id, atv.account_type, atv.account_name, atv.date, atv.rbs_year, atv.rbs_month, atv.rbs_months_ago, atv.rbs_month_name, atv.balance "
						  + "FROM all_transactions_view atv, monthly_balance_transactions mbt "
						  + "WHERE atv.transaction_id = mbt.transaction_id";

		return strDdlCommands;
	}
	
	static void populateTablesWithBaseData(Connection dbConnection,
										   List<List<String>> contentOfAllRbsDataFiles,
										   List<List<String>> contentOfAllBosDataFiles,
										   List<List<String>> contentOfAllBos2DataFiles,
										   List<List<String>> contentOfAllBosCcDataFiles,
										   List<List<String>> contentOfAllAmexDataFiles,
										   List<List<String>> contentOfAllYbDataFiles,
										   List<List<String>> contentOfAllOtherDataFiles,
										   List<List<String>> contentOfCategoriesFile,
										   List<List<String>> contentOfIgnoreTransFile, 
										   List<List<String>> contentOfAccountsFile) throws SQLException, ParseException
	{
		for (List<String> tableRow : contentOfAllRbsDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "					        
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance, "
					        + "account_name, "
					        + "account_number"
					        + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"1");
			ps.setString(4,tableRow.get(2));
			ps.setString(5,tableRow.get(3));
			ps.setString(6,tableRow.get(4));
			ps.setString(7,tableRow.get(5));
			ps.setString(8,tableRow.get(6));
			ps.setString(9,tableRow.get(7));
			ps.setString(10,tableRow.get(8));
			
			ps.executeUpdate();
		}

		for (List<String> tableRow : contentOfAllBosDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "		
					        + "date, "
					        + "type, "
					        + "account_name, "
					        + "account_number, "
					        + "description, "
					        + "value, "
					        + "balance"
					        + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			String bosValue = null;
			
			if (!tableRow.get(7).equals("") && !tableRow.get(7).substring(0,1).equals("-"))
			{
				bosValue = "-"+tableRow.get(7);
			}
			else if (!tableRow.get(7).equals("") && tableRow.get(7).substring(0,1).equals("-"))
			{
				bosValue = tableRow.get(7);
			}
			else
			{
				bosValue = tableRow.get(8);
			}
			
			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"2");
			ps.setString(4,tableRow.get(2));
			ps.setString(5,tableRow.get(3));
			ps.setString(6,tableRow.get(4));
			ps.setString(7,tableRow.get(5));
			ps.setString(8,tableRow.get(6));
			ps.setString(9,bosValue);
			ps.setString(10,tableRow.get(9));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfAllBos2DataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "		
					        + "date, "
					        + "type, "
					        + "account_name, "
					        + "account_number, "
					        + "description, "
					        + "value, "
					        + "balance"
					        + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);

			String bosValue = null;
			String bosDate = tableRow.get(2);
			String bosDate2 = null;
			String bosMonth = null;
			
			if (!Character.isDigit(bosDate.charAt(4)))
			{	
				if (bosDate.substring(3,6).equals("Jan")) bosMonth = "01";
				if (bosDate.substring(3,6).equals("Feb")) bosMonth = "02";
				if (bosDate.substring(3,6).equals("Mar")) bosMonth = "03";
				if (bosDate.substring(3,6).equals("Apr")) bosMonth = "04";
				if (bosDate.substring(3,6).equals("May")) bosMonth = "05";
				if (bosDate.substring(3,6).equals("Jun")) bosMonth = "06";
				if (bosDate.substring(3,6).equals("Jul")) bosMonth = "07";
				if (bosDate.substring(3,6).equals("Aug")) bosMonth = "08";
				if (bosDate.substring(3,6).equals("Sep")) bosMonth = "09";
				if (bosDate.substring(3,6).equals("Oct")) bosMonth = "10";
				if (bosDate.substring(3,6).equals("Nov")) bosMonth = "11";
				if (bosDate.substring(3,6).equals("Dec")) bosMonth = "12";
				
				bosDate2 = bosDate.substring(0,3)+bosMonth+bosDate.substring(6);
			}
			else
			{
				bosDate2 = tableRow.get(2);
			}

			if (!tableRow.get(7).equals(""))
			{
				bosValue = tableRow.get(7);
			}
			else
			{
				bosValue = tableRow.get(8);
			}
			
			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"2");
			ps.setString(4,bosDate2);
			ps.setString(5,tableRow.get(3));
			ps.setString(6,tableRow.get(4));
			ps.setString(7,tableRow.get(5));
			ps.setString(8,tableRow.get(6));
			ps.setString(9,bosValue);
			ps.setString(10,tableRow.get(9));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfAllBosCcDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "	
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value"
					        + ") VALUES (?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"8");
			ps.setString(4,tableRow.get(3));
			ps.setString(5,"MCC");
			ps.setString(6,tableRow.get(5));
			ps.setString(7,tableRow.get(6));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfAllAmexDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "	
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value"
					        + ") VALUES (?,?,?,?,?,?,?)";

			PreparedStatement ps = dbConnection.prepareStatement(psString);

			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"10");
			ps.setString(4,tableRow.get(2));
			ps.setString(5,"AMX");
			ps.setString(6,tableRow.get(5));
			ps.setString(7,tableRow.get(4));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfAllYbDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "	
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value"
					        + ") VALUES (?,?,?,?,?,?,?)";

			PreparedStatement ps = dbConnection.prepareStatement(psString);

			String ybDate = tableRow.get(2);
			String ybDate2 = null;
			String ybMonth = null;

			if (ybDate.substring(3,6).equals("Jan")) ybMonth = "01";
			if (ybDate.substring(3,6).equals("Feb")) ybMonth = "02";
			if (ybDate.substring(3,6).equals("Mar")) ybMonth = "03";
			if (ybDate.substring(3,6).equals("Apr")) ybMonth = "04";
			if (ybDate.substring(3,6).equals("May")) ybMonth = "05";
			if (ybDate.substring(3,6).equals("Jun")) ybMonth = "06";
			if (ybDate.substring(3,6).equals("Jul")) ybMonth = "07";
			if (ybDate.substring(3,6).equals("Aug")) ybMonth = "08";
			if (ybDate.substring(3,6).equals("Sep")) ybMonth = "09";
			if (ybDate.substring(3,6).equals("Oct")) ybMonth = "10";
			if (ybDate.substring(3,6).equals("Nov")) ybMonth = "11";
			if (ybDate.substring(3,6).equals("Dec")) ybMonth = "12";
			
			ybDate2 = ybDate.substring(0,3)+ybMonth+ybDate.substring(6);

			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,"18");
			ps.setString(4,ybDate2);
			ps.setString(5,"YB");
			ps.setString(6,tableRow.get(5));
			ps.setString(7,tableRow.get(3));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfAllOtherDataFiles)
		{
			String psString = "INSERT INTO text_files_varchar ("
					        + "file_id, "
					        + "line_id, "
					        + "account_id, "					        
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance, "
					        + "account_name, "
					        + "account_number"
					        + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
	
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setString(1,tableRow.get(0));
			ps.setString(2,tableRow.get(1));
			ps.setString(3,tableRow.get(3));
			ps.setString(4,tableRow.get(2));
			ps.setString(5,tableRow.get(4));
			ps.setString(6,tableRow.get(5));
			ps.setString(7,tableRow.get(6));
			ps.setString(8,tableRow.get(7));
			ps.setString(9,tableRow.get(8));
			ps.setString(10,tableRow.get(9));
			
			ps.executeUpdate();
		}
		
		for (List<String> tableRow : contentOfCategoriesFile)
		{
			String psString = "INSERT INTO categories "
							+ "(category_name, sub_category_name, sort_order) ("
							+ "SELECT ?,?,? "
					        + "FROM categories "
					        + "WHERE category_name = ? AND sub_category_name = ? AND sort_order = ?"
					        + "HAVING COUNT(*) = 0)";

			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setString(1,tableRow.get(3));
			ps.setString(2,tableRow.get(4));
			ps.setInt(3,Integer.parseInt(tableRow.get(2)));
			ps.setString(4,tableRow.get(3));
			ps.setString(5,tableRow.get(4));
			ps.setInt(6,Integer.parseInt(tableRow.get(2)));
			ps.executeUpdate();

			String psString2 = "INSERT INTO category_map ("
					         + "category_id, "
					         + "pattern_match, "
					         + "trans_type"
					         + ") VALUES (("
					         + "SELECT category_id FROM categories "
					         + "WHERE category_name = ? "
					         + "AND sub_category_name = ?"
					         + "),?,?)";
		
			PreparedStatement ps2 = dbConnection.prepareStatement(psString2);
			
			ps2.setString(1,tableRow.get(3));
			ps2.setString(2,tableRow.get(4));
			ps2.setString(3,tableRow.get(5));
			ps2.setString(4,tableRow.get(6));
			
			ps2.executeUpdate();
		}

		for (List<String> tableRow : contentOfIgnoreTransFile)
		{
			String psString = "INSERT INTO ignore_transactions "
							+ "(date, type, description, value, balance, account_name, account_number) "
							+ "VALUES (?,?,?,?,?,?,?)";
						
        	PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(tableRow.get(2));
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
			BigDecimal bigDecValue = new BigDecimal(tableRow.get(5));
			BigDecimal bigDecBalance = new BigDecimal(tableRow.get(6));
			
			ps.setDate(1,sqlDate);
			ps.setString(2,tableRow.get(3));
			ps.setString(3,tableRow.get(4));
			ps.setBigDecimal(4,bigDecValue);
			ps.setBigDecimal(5,bigDecBalance);
			ps.setString(6,tableRow.get(7));
			ps.setString(7,tableRow.get(8));
			ps.executeUpdate();
		}

		for (List<String> tableRow : contentOfAccountsFile)
		{
			String psString = "INSERT INTO accounts "
							+ "(account_id, bank, sort_code, account_number, account_type, account_name, customer_number) "
							+ "VALUES (?,?,?,?,?,?,?)";
						
        	PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setInt(1,Integer.parseInt(tableRow.get(2)));
			ps.setString(2,tableRow.get(3));
			ps.setString(3,tableRow.get(4));
			ps.setString(4,tableRow.get(5));
			ps.setString(5,tableRow.get(6));
			ps.setString(6,tableRow.get(7));
			ps.setString(7,tableRow.get(8));
			
			ps.executeUpdate();
		}
	}

	static void transformData(Connection dbConnection) throws SQLException
	{
	    // Populate text_files_type table
		String insertStatement = "INSERT INTO text_files_type"
		  		  			   + "("
					  		   + "file_id, "
						       + "line_id, "
					  		   + "account_id, "
							   + "date, "
							   + "type, "
							   + "description, "
							   + "value, "
							   + "balance, "
							   + "account_name, "
							   + "account_number"
							   + ") "
							   + "SELECT "
							   + "cast(file_id as integer), "
							   + "cast(line_id as integer), "
							   + "cast(account_id as integer), "
							   + "cast(SUBSTR(date,7,4) || '-' || SUBSTR(date,4,2) || '-' || SUBSTR(date,1,2) as date), "
							   + "type, "
							   + "description, "
							   + "cast(value as decimal(10,2)), "
							   + "cast(balance as decimal(10,2)), "
							   + "account_name, "
							   + "account_number "
							   + "FROM text_files_varchar "
							   + "WHERE date NOT LIKE '%Date%' AND date <> '' "; // Ignore rows that are not real transactions

		Statement statement = null;
		statement = dbConnection.createStatement();
		statement.execute(insertStatement);
	    
	    // Populate all_transactions table and check data integrity
		boolean fileOk = populateAllTransactionsTable(dbConnection);
		System.out.println("file ok: " + fileOk);		
	}

	// Populate all_transactions table and check data integrity
	static boolean populateAllTransactionsTable(Connection dbConnection) throws SQLException
	{
		boolean fileOk = true;
		
		// Populate all_transactions table with OTHER transactions
        String queryOther = "SELECT * FROM text_files_type "
        		          + "WHERE type IN ('BAL','ADJ') "
        		          + "ORDER BY date";
        
        Statement statementOther = dbConnection.createStatement();
        ResultSet rsOther = statementOther.executeQuery(queryOther);
        
        while (rsOther.next())
        {
    		String psString = "INSERT INTO all_transactions ("
			        		+ "file_id, "
					        + "line_id, "
			        		+ "account_id, "
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance"
					        + ") VALUES (?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setInt(1,rsOther.getInt("FILE_ID"));
			ps.setInt(2,rsOther.getInt("LINE_ID"));
			ps.setInt(3,rsOther.getInt("ACCOUNT_ID"));
			ps.setDate(4,rsOther.getDate("DATE"));
			ps.setString(5,rsOther.getString("TYPE"));
			ps.setString(6,rsOther.getString("DESCRIPTION"));
			ps.setBigDecimal(7,rsOther.getBigDecimal("VALUE"));
			ps.setBigDecimal(8,rsOther.getBigDecimal("BALANCE"));
			
			ps.executeUpdate();

    		fileOk = true;
    	}

        System.out.println("OTHER file ok? " + fileOk);

		// Populate all_transactions table with RBS transactions and check data integrity
		boolean firstLine = true;
        BigDecimal previousLineBalance = new BigDecimal(0);
        BigDecimal currentLineBalance = null;
        BigDecimal currentLineValue = null;

        String query = "SELECT * FROM text_files_type "
        		     + "WHERE account_id = 1 "
        		     + "ORDER BY date, line_id, file_id";
        
        Statement statement = dbConnection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        while (rs.next())
        {
        	currentLineValue = rs.getBigDecimal("VALUE");
        	currentLineBalance = rs.getBigDecimal("BALANCE");
        	
    		if (currentLineBalance.equals(currentLineValue.add(previousLineBalance)) || firstLine)
    		{
    			String psString = "INSERT INTO all_transactions ("
				        		+ "file_id, "
						        + "line_id, "
				        		+ "account_id, "
						        + "date, "
						        + "type, "
						        + "description, "
						        + "value, "
						        + "balance"
						        + ") VALUES (?,?,?,?,?,?,?,?)";
    			
    			PreparedStatement ps = dbConnection.prepareStatement(psString);
    			
    			ps.setInt(1,rs.getInt("FILE_ID"));
    			ps.setInt(2,rs.getInt("LINE_ID"));
    			ps.setInt(3,rs.getInt("ACCOUNT_ID"));
    			ps.setDate(4,rs.getDate("DATE"));
    			ps.setString(5,rs.getString("TYPE"));
    			ps.setString(6,rs.getString("DESCRIPTION"));
    			ps.setBigDecimal(7,rs.getBigDecimal("VALUE"));
    			ps.setBigDecimal(8,rs.getBigDecimal("BALANCE"));
    			
    			ps.executeUpdate();
    			                    
        		previousLineBalance = currentLineBalance;
        		fileOk = true;
    		}
    		else
    		{
    			// This checks that the last two lines that are processed are not duplicates of each other
    			// This could happen if a new file of transactions was downloaded from the bank
    			// but there were no new transactions in it but there was overlap of old ones
    			if (!currentLineBalance.equals(previousLineBalance)) fileOk = false;
    		}
    		firstLine = false;
    	}

        System.out.println("RBS file ok? " + fileOk);
        
		// Populate all_transactions table with BOS transactions and check data integrity
        firstLine = true;
        BigDecimal previousLineBalanceBos = new BigDecimal(0);
        BigDecimal currentLineBalanceBos = null;
        BigDecimal currentLineValueBos = null;

        String queryBos = "SELECT * FROM text_files_type "
        				+ "WHERE account_id = 2 "
        		        + "ORDER BY date, line_id DESC, file_id";
        
        Statement statementBos = dbConnection.createStatement();
	    ResultSet rsBos = statementBos.executeQuery(queryBos);
	    
        while (rsBos.next())
        {
        	currentLineValueBos = rsBos.getBigDecimal("VALUE");
        	currentLineBalanceBos = rsBos.getBigDecimal("BALANCE");
        	
    		if (currentLineBalanceBos.equals(currentLineValueBos.add(previousLineBalanceBos)) || firstLine)
    		{
    			String psString = "INSERT INTO all_transactions ("
				        		+ "file_id, "
						        + "line_id, "
				        		+ "account_id, "
						        + "date, "
						        + "type, "
						        + "description, "
						        + "value, "
						        + "balance"
						        + ") VALUES (?,?,?,?,?,?,?,?)";
    			
    			PreparedStatement ps = dbConnection.prepareStatement(psString);
    			
    			ps.setInt(1,rsBos.getInt("FILE_ID"));
    			ps.setInt(2,rsBos.getInt("LINE_ID"));
    			ps.setInt(3,rsBos.getInt("ACCOUNT_ID"));
    			ps.setDate(4,rsBos.getDate("DATE"));
    			ps.setString(5,rsBos.getString("TYPE"));
    			ps.setString(6,rsBos.getString("DESCRIPTION"));
    			ps.setBigDecimal(7,rsBos.getBigDecimal("VALUE"));
    			ps.setBigDecimal(8,rsBos.getBigDecimal("BALANCE"));
    			
    			ps.executeUpdate();
    		  
    	        previousLineBalanceBos = currentLineBalanceBos;
    	        fileOk = true;
    		}
    		else
    		{
    			// This checks that the last two lines that are processed are not duplicates of each other
    			// This could happen if a new file of transactions was downloaded from the bank
    			// but there were no new transactions in it but there was overlap of old ones
    			if (!currentLineBalance.equals(previousLineBalance)) fileOk = false;
    		}
    		firstLine = false;
    	}
        
        System.out.println("BOS file ok? " + fileOk);
		
        // Populate all_transactions table with BOS Credit Card transactions and check data integrity
        firstLine = true;
        BigDecimal previousLineBalanceBosCc = new BigDecimal(0);
        BigDecimal currentLineBalanceBosCc = null;
        BigDecimal currentLineValueBosCc = null;
        BigDecimal negateValue = null;
        BigDecimal negateBalance = null;
        
        String queryBosCc = "SELECT * FROM text_files_type "
        				  + "WHERE account_id = 8 "
        				  + "ORDER BY date, line_id DESC, file_id";
        
        Statement statementBosCc = dbConnection.createStatement();
	    ResultSet rsBosCc = statementBosCc.executeQuery(queryBosCc);
	    
        while (rsBosCc.next())
        {
        	currentLineValueBosCc = rsBosCc.getBigDecimal("VALUE");
        	currentLineBalanceBosCc = currentLineValueBosCc.add(previousLineBalanceBosCc);
				
			String psString = "INSERT INTO all_transactions ("
			        		+ "file_id, "
					        + "line_id, "
			        		+ "account_id, "
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance "
					        + ") VALUES (?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			// Negate credit card statement value so that purchases appear on reports as a negative like other accounts
			negateValue = rsBosCc.getBigDecimal("VALUE").multiply(new BigDecimal(-1));
			negateBalance = currentLineBalanceBosCc.multiply(new BigDecimal(-1));
			
			ps.setInt(1,rsBosCc.getInt("FILE_ID"));
			ps.setInt(2,rsBosCc.getInt("LINE_ID"));
			ps.setInt(3,rsBosCc.getInt("ACCOUNT_ID"));
			ps.setDate(4,rsBosCc.getDate("DATE"));
			ps.setString(5,rsBosCc.getString("TYPE"));
			ps.setString(6,rsBosCc.getString("DESCRIPTION"));
			ps.setBigDecimal(7,negateValue);
			ps.setBigDecimal(8,negateBalance);
			
			ps.executeUpdate();
			
	        previousLineBalanceBosCc = currentLineBalanceBosCc;
	        fileOk = true;

	        // write code here to check for duplicate trans
	        // if (!currentLineBalance.equals(previousLineBalance)) fileOk = false;

    	}
           
        System.out.println("BOS Credit Card file ok? " + fileOk);
        
        // Populate all_transactions table with Amex Card transactions and check data integrity
        firstLine = true;
        BigDecimal previousLineBalanceAmex = new BigDecimal(0);
        BigDecimal currentLineBalanceAmex = null;
        BigDecimal currentLineValueAmex = null;
        BigDecimal negateValueAmex = null;
        BigDecimal negateBalanceAmex = null;
        
        String queryAmex = "SELECT * FROM text_files_type "
        				  + "WHERE account_id = 10 "
        				  + "ORDER BY date, line_id DESC, file_id";
        
        Statement statementAmex = dbConnection.createStatement();
	    ResultSet rsAmex = statementAmex.executeQuery(queryAmex);
	    
        while (rsAmex.next())
        {
        	currentLineValueAmex = rsAmex.getBigDecimal("VALUE");
        	currentLineBalanceAmex = currentLineValueAmex.add(previousLineBalanceAmex);
				
			String psString = "INSERT INTO all_transactions ("
			        		+ "file_id, "
					        + "line_id, "
			        		+ "account_id, "
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance "
					        + ") VALUES (?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			// Negate Amex card statement value so that purchases appear on reports as a negative like other accounts
			negateValueAmex = rsAmex.getBigDecimal("VALUE").multiply(new BigDecimal(-1));
			negateBalanceAmex = currentLineBalanceAmex.multiply(new BigDecimal(-1));
			
			ps.setInt(1,rsAmex.getInt("FILE_ID"));
			ps.setInt(2,rsAmex.getInt("LINE_ID"));
			ps.setInt(3,rsAmex.getInt("ACCOUNT_ID"));
			ps.setDate(4,rsAmex.getDate("DATE"));
			ps.setString(5,rsAmex.getString("TYPE"));
			ps.setString(6,rsAmex.getString("DESCRIPTION"));
			ps.setBigDecimal(7,negateValueAmex);
			ps.setBigDecimal(8,negateBalanceAmex);
			
			ps.executeUpdate();
			
	        previousLineBalanceAmex = currentLineBalanceAmex;
	        fileOk = true;

	        // write code here to check for duplicate trans
	        // if (!currentLineBalance.equals(previousLineBalance)) fileOk = false;

    	}
        
        System.out.println("Amex Credit Card file ok? " + fileOk);
        
        // Populate all_transactions table with Yorkshire Credit Card transactions and check data integrity
        firstLine = true;
        BigDecimal previousLineBalanceYb = new BigDecimal(0);
        BigDecimal currentLineBalanceYb = null;
        BigDecimal currentLineValueYb = null;
        
        String queryYb = "SELECT * FROM text_files_type "
        				  + "WHERE account_id = 18 "
        				  + "ORDER BY date, line_id DESC, file_id";
        
        Statement statementYb = dbConnection.createStatement();
	    ResultSet rsYb = statementYb.executeQuery(queryYb);
	    
        while (rsYb.next())
        {
        	currentLineValueYb = rsYb.getBigDecimal("VALUE");
        	currentLineBalanceYb = currentLineValueYb.add(previousLineBalanceYb);
				
			String psString = "INSERT INTO all_transactions ("
			        		+ "file_id, "
					        + "line_id, "
			        		+ "account_id, "
					        + "date, "
					        + "type, "
					        + "description, "
					        + "value, "
					        + "balance "
					        + ") VALUES (?,?,?,?,?,?,?,?)";
			
			PreparedStatement ps = dbConnection.prepareStatement(psString);
			
			ps.setInt(1,rsYb.getInt("FILE_ID"));
			ps.setInt(2,rsYb.getInt("LINE_ID"));
			ps.setInt(3,rsYb.getInt("ACCOUNT_ID"));
			ps.setDate(4,rsYb.getDate("DATE"));
			ps.setString(5,rsYb.getString("TYPE"));
			ps.setString(6,rsYb.getString("DESCRIPTION"));
			ps.setBigDecimal(7,rsYb.getBigDecimal("VALUE"));
			ps.setBigDecimal(8,currentLineBalanceYb);
			
			ps.executeUpdate();
			
	        previousLineBalanceYb = currentLineBalanceYb;
	        fileOk = true;

	        // write code here to check for duplicate trans
	        // if (!currentLineBalance.equals(previousLineBalance)) fileOk = false;

    	}
        
        System.out.println("YB Credit Card file ok? " + fileOk);
        
        // Populate all_transactions table with category information
    	try
		{
			String psString1 = "UPDATE all_transactions t "
							 + "SET category_id = ("
							 + "SELECT category_id FROM category_map_view "
							 + "WHERE t.description LIKE pattern_match "
							 + "AND trans_type NOT IN ('BAC','CHG','INT','C/L','CHQ','PAY','CPT','MCC')) "
							 + "WHERE type NOT IN ('BAC','CHG','INT','C/L','CHQ','PAY','CPT','MCC')";
			
			PreparedStatement ps1 = dbConnection.prepareStatement(psString1);
		
			ps1.executeUpdate();
			
			String psString2 = "UPDATE all_transactions t "
					 		 + "SET category_id = ("
					 		 + "SELECT category_id FROM category_map_view "
					 		 + "WHERE t.type = trans_type) "
			 				 + "WHERE type IN ('CHG','INT','C/L','CHQ','PAY','DEB','CPT') "
					 		 + "AND category_id IS NULL";
			 
			PreparedStatement ps2 = dbConnection.prepareStatement(psString2);

			ps2.executeUpdate();
			
			String psString3 = "UPDATE all_transactions t "
					 		 + "SET category_id = ("
					 		 + "SELECT cmv.category_id FROM category_map_view cmv "
					 		 + "WHERE t.type = cmv.trans_type "
					 		 + "AND t.description LIKE cmv.pattern_match) "
			 				 + "WHERE t.type IN ('BAC','MCC')";
			
			PreparedStatement ps3 = dbConnection.prepareStatement(psString3);

			ps3.executeUpdate();
			
			String psString6 = "UPDATE all_transactions t "
							 + "SET category_id = ("
							 + "SELECT category_id FROM categories "
							 + "WHERE category_name = 'Spend' "
							 + "AND sub_category_name = 'Miscellaneous') "
							 + "WHERE "
							 + "("
							 + "description LIKE '9598%' "
							 + "OR description LIKE '8808%' "
							 + "OR description LIKE '5668%' "
							 + "OR description LIKE '5646%' "
							 + "OR description LIKE '3187%' "
							 + "OR description LIKE '2867%' "
							 + "OR description LIKE '2463%' "
							 + "OR description LIKE '2416%' "
							 + "OR description LIKE '2370%' "
							 + "OR description LIKE '1669%' "
							 + "OR description LIKE '1586%' "
							 + "OR description LIKE '%CD 8211%'"
							 + ") "
							 + "AND category_id IS NULL"
							 ;

			PreparedStatement ps6 = dbConnection.prepareStatement(psString6);

			ps6.executeUpdate();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			System.err.println("");
			System.err.println("one to many category mapping"); 
			
			String query2 = "SELECT description, balance, count(*) "
						  + "FROM all_transactions t, category_map cm "
						  + "WHERE t.description like cm.pattern_match "
						  + "GROUP BY description, balance "
						  + "HAVING count(*) > 1";
			
	        ResultSet rs2 = statement.executeQuery(query2);
	        
	        while (rs2.next())
	        {
	        	System.err.println(rs2.getString("DESCRIPTION") + "..." + rs2.getBigDecimal("BALANCE"));
	        }

		}
		
		return fileOk;
	}
	
	static void deleteDatabase(File databaseDir) throws IOException
	{
		File[] contents = databaseDir.listFiles();
		
		if (contents != null) 
		{
			for (File f : contents) 
			{
				deleteDatabase(f);
		    }
		}
		
		databaseDir.delete();

	}	
}
