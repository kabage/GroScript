import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GroCli {

	private static final Logger log = Logger.getLogger(GroCli.class.getName());

	String primer_quote = " Here's what's going to happen."
			+ "I'm going to read this..." + "and you're going to Iisten,"
			+ "and you're going to stay on the Iine."
			+ "You're not going to interrupt."
			+ " You're not going to speak for any reason."
			+ "Now, some of this you know."
			+ "I'm going to start at the top of the page."
			+ "MeticuIous, yes. Methodical. Educated."
			+ "They were these things."
			+ "Nothing extreme. Like anyone, they varied."
			+ "There were days of mistakes" + "and Iaziness and infighting."
			+ " And there were days, good days,"
			+ " when by anyone's judgment..."
			+ "they wouId have to be considered clever." + "No one wouId say"
			+ " that what they were doing was compIicated."
			+ "It wouIdn't even be considered new."
			+ "Except maybe in the geologicaI sense."
			+ "They took from their surroundings" + " what was needed..."
			+ " and made of it something more.  ";

	private String[] args = null;
	String database_name, database_host, database_user, database_password,
			start_date, end_date;
	private Options options = new Options();
	CommandLine cmd;

	public GroCli(String[] args) {

		this.args = args;

		options.addOption(null, "start_date", true, "the start date");
		options.addOption(null, "end_date", true, "the end date");
		options.addOption(null, "database_name", true, "the database name");
		options.addOption(null, "database_host", true, "the database host");
		options.addOption(null, "database_user", true, "Username");
		options.addOption(null, "database_pass", true, "password");
		options.addOption(null, "help", false, "help");
	}

	public void parse() {

		CommandLineParser parser = new BasicParser();

		try {

			cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				help();

			} else {
				System.out.println(primer_quote);

				database_name = cmd.getOptionValue("database_name");
				database_host = cmd.getOptionValue("database_host");
				database_user = cmd.getOptionValue("database_user");
				database_password = cmd.getOptionValue("database_pass");
				start_date = cmd.getOptionValue("start_date");
				end_date = cmd.getOptionValue("end_date");

				Db.downloadFile("ftp://ftp.nass.usda.gov/quickstats/qs.crops_20160305.txt.gz");
				UncompressFile.uncompress();
				Db.createDbAndTables(Db.connectToDb(database_host,
						database_name, database_user, database_password));

				Db.parseCSVLib("/home/qs.crops.txt",
						Integer.valueOf(start_date.substring(0, 4)),
						Integer.valueOf(end_date.substring(0, 4)));

			}

		} catch (ParseException e) {

			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help();
		}
	}

	private void help() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);
		System.exit(0);

	}

}