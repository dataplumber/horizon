//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.archive.tool;

import gov.nasa.horizon.archive.core.ArchiveProperty;
import gov.nasa.horizon.archive.core.Command;

import java.util.Iterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a command line tool to allow archive capabilities such as 
 * verify, delete, or move archived metadata/data.
 *
 * @author clwong
 * $Id: ArchiveTool.java 8160 2011-08-02 23:59:35Z gangl $
 */
public class ArchiveTool {
	private enum CommandType { VERIFY, DELETE, RELOCATE, ROLLING_STORE,LOCATE, REASSOCIATE, HELP, UNKNOWN }
	private static Log log = LogFactory.getLog(ArchiveTool.class);;
	private static Options mainOptions;
	private static Options helpOptions = new Options();
	private static Options verifyOptions;
	private static Options locateOptions;
	private static Options deleteOptions;
	private static Options relocateOptions;
	private static Options reassociateOptions;
	private static Options rollingOptions;

	/**
	 * Perform initial parsing and classify.
	 * 
	 * @param args Command line arguments
	 * @return CommandType
	 * 
	 */
	private static CommandType classifyCommand(String args[]) {
		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (arg.equals("-help") || arg.equals("-h")) {
					return CommandType.HELP;
				}
			}
			for (String arg : args) {
				if (arg.equals("-verify")) {
					return CommandType.VERIFY;
				}
				if (arg.equals("-delete")) {
					return CommandType.DELETE;
				}
				if (arg.equals("-relocate")) {
					return CommandType.RELOCATE;
				}
				if (arg.equals("-locate")) {
					return CommandType.LOCATE;
				}
				if (arg.equals("-rolling_store")) {
					return CommandType.ROLLING_STORE;
				}
				if (arg.equals("-reassociate")) {
					return CommandType.REASSOCIATE;
				}
			}
		}
		return CommandType.UNKNOWN;
	}
	
	@SuppressWarnings("static-access")
	private static OptionGroup createProductTypeOptions() {
		OptionGroup optionGroup = new OptionGroup( );		
		optionGroup.addOption(OptionBuilder.withArgName( "productTypeId" )
				.withType(new Integer(0))
                .hasArg()
                .withLongOpt("productTypeId")
                .withDescription("Product Type Id" )
                .create("pt"));
		// Take these options out because it can be huge impact.
		optionGroup.addOption( OptionBuilder.withArgName( "id1,id2,id3,..." )
                .hasArgs()
                .withValueSeparator(',')
                .withDescription("List of pt id" )
                .create("dl") );
		optionGroup.addOption( OptionBuilder.withArgName( "beginId:endId" )
                .hasArgs(2)
                .withValueSeparator(':')
                .withDescription("Range of pt id" )
                .create("dr") );
                
		return optionGroup;
	}

	@SuppressWarnings("static-access")
	private static Options createDeleteCmdOptions() {
		// delete command
		// java CommandLine delete
		Options options = new Options();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("delete"));
		optionGroup.setRequired(true);
		options.addOptionGroup(optionGroup);
		options.addOptionGroup(createEntityOptions());
		options.addOption(createIndexOption());
		options.addOption(OptionBuilder.hasArg(false)
				.withLongOpt("data-only")
                .withDescription("Delete archived data only;" )
                .create());
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print delete usage" )
                .create("help"));
		return options;
	}
	
	private static OptionGroup createEntityOptions() {
		OptionGroup optionGroup = new OptionGroup();
		Iterator it = createProductOptions().getOptions().iterator();
		while (it.hasNext()) optionGroup.addOption((Option) it.next());
		it = createProductTypeOptions().getOptions().iterator();
		while (it.hasNext()) optionGroup.addOption((Option) it.next());
		optionGroup.setRequired(true);
		return optionGroup;
	}

	@SuppressWarnings("static-access")
	private static OptionGroup createProductOptions() {
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption( OptionBuilder.withArgName( "productId" )
                .hasArg()
                .withLongOpt("productId")
                .withDescription("Product Id" )
                .create("p"));	
		optionGroup.addOption( OptionBuilder.withArgName( "id1,id2,id3,..." )
                .hasArgs()
                .withValueSeparator(',')
                .withDescription("List of product id" )
                .create("pl") );
		optionGroup.addOption( OptionBuilder.withArgName( "beginId:endId" )
                .hasArgs(2)
                .withValueSeparator(':')
                .withDescription("Range of product id" )
                .create("pr") );
		optionGroup.setRequired(true);
		return optionGroup;
	}

	@SuppressWarnings("static-access")
	private static Options createHelpCmdOptions() {
		// help command
		// java CommandLine help
		
		Options options = new Options();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("verify"));
		optionGroup.addOption(mainOptions.getOption("delete"));
		optionGroup.addOption(mainOptions.getOption("relocate"));
		optionGroup.addOption(mainOptions.getOption("locate"));
		optionGroup.addOption(mainOptions.getOption("rolling_store"));
		optionGroup.addOption(mainOptions.getOption("reassociate"));
		options.addOptionGroup(optionGroup);
		options.addOption(mainOptions.getOption("help"));
		return options;
	}
	
	@SuppressWarnings("static-access")
	private static Option createIndexOption() {
		return OptionBuilder.withArgName( "beginIndex:endIndex" )
				.withLongOpt("index")
                .hasArgs(2)
                .withValueSeparator(':')
                .withDescription("Begin & end Index to use with productTypeId;" )
                .create("i");
	}

	@SuppressWarnings("static-access")
	private static void createOptions() {
		// Create options for the following commands
		// java CommandLine verify
		// java CommandLine delete
		// java CommandLine relocate
		// Individual classes can be created for each command if this tool
		// is extended to support more commands
		mainOptions = new Options();
		mainOptions.addOption(OptionBuilder.hasArg(false)
                .withDescription("print usage" )
                .create("help"));
		
		OptionGroup optionGroup = new OptionGroup( );
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Verify the archived entity by productId or productTypeId;" )
                .create("verify"));
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Delete the archived entity by productId;" )
                .create("delete"));
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Relocate the archived entity by productId;" )
                .create("relocate"));
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Locate product IDs based on name or start/stop times;" )
                .create("locate"));
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Scan for and process rolling store data;" )
                .create("rolling_store"));
		optionGroup.addOption(OptionBuilder.hasArg(false)
                .withDescription("Reassociate products from one product type to another;" )
                .create("reassociate"));
		//optionGroup.addOption(mainOptions.getOption("help"));
		optionGroup.setRequired(true);
		mainOptions.addOptionGroup(optionGroup);
		optionGroup = createEntityOptions();
		//optionGroup.setRequired(false);
		mainOptions.addOptionGroup(optionGroup);	

		verifyOptions = createVerifyCmdOptions();
		locateOptions = createLocateCmdOptions();
		deleteOptions = createDeleteCmdOptions();
		relocateOptions = createRelocateCmdOptions();
		reassociateOptions = createReassociateCmdOptions();
		rollingOptions = createRollingCmdOptions();
		helpOptions = createHelpCmdOptions();
	}
	
	@SuppressWarnings("static-access")
	private static Options createRelocateCmdOptions() {
		// relocate command
		// java CommandLine relocate
		Options options = new Options();	
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("relocate"));
		optionGroup.setRequired(true);
		options.addOptionGroup(optionGroup);
		
		options.addOptionGroup(createEntityOptions());
		options.addOption(createIndexOption());
		
		optionGroup = new OptionGroup();
		optionGroup.addOption(OptionBuilder.withArgName("basepath")
				.hasArg()
				.withLongOpt("basepath")
                .withDescription("Base Path Location" )
                .create("bp"));	
		optionGroup.setRequired(true);
				
		options.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		optionGroup.addOption(OptionBuilder.withArgName("force")
				.hasArg(false)
				.withLongOpt("force")
                .withDescription("Force the relocation of products without prompting user" )
                .create("f"));	
		optionGroup.setRequired(false);
		options.addOptionGroup(optionGroup);
		
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print verify usage" )
                .create("help"));	
		return options;
	}

	@SuppressWarnings("static-access")
	private static Options createRollingCmdOptions() {
		// relocate command
		// java CommandLine rolling_store
		Options options = new Options();	
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("rolling_store"));
		optionGroup.setRequired(true);
		options.addOptionGroup(optionGroup);
		options.addOptionGroup(createEntityOptions());
		options.addOption(createIndexOption());
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print rolling_store usage" )
                .create("help"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("start-date")
                .withDescription("A start date (MM/dd/yyyy) with which only products after should be processed." )
                .create("start"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("stop-date")
                .withDescription("A stop date (MM/dd/yyyy) with which only products before should be processed." )
                .create("stop"));
		return options;
	}
	@SuppressWarnings("static-access")
	private static Options createReassociateCmdOptions() {
		// relocate command
		// java CommandLine rolling_store
		Options options = new Options();	
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("reassociate"));
		optionGroup.setRequired(true);
		//options.addOptionGroup(optionGroup);
		//options.addOptionGroup(createEntityOptions());
		//options.addOption(createIndexOption());
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("Reassociate Command" )
                .create("reassociate"));
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print Reassociate usage" )
                .create("help"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("pattern")
                .withDescription("A pattern to search product names on, using '#' as wildcard characters." )
                .create("pattern"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("from-productType")
                .withDescription("The product type to which the products currently belong." )
                .create("fpt"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("to-productType")
                .withDescription("The product type to which the products will be moved." )
                .create("tpt"));
		options.addOption(OptionBuilder.hasArg(false)
				.withLongOpt("test-only")
                .withDescription("Test only mode will not move files or change metadata. Only prints out the new paths for a product." )
                .create("test"));
		return options;
	}
	
	@SuppressWarnings("static-access")
	private static Options createLocateCmdOptions() {
		// relocate command
		// java CommandLine rolling_store
		Options options = new Options();	
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("locate"));
		optionGroup.setRequired(true);
		options.addOptionGroup(optionGroup);
		options.addOptionGroup(createEntityOptions());
		options.addOption(createIndexOption());
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print locate usage" )
                .create("help"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("output")
                .withDescription("Full path to outputfile" )
                .create("output"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("start-date")
                .withDescription("A start date (MM/dd/yyyy) with which only products after should be processed." )
                .create("start"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("stop-date")
                .withDescription("A stop date (MM/dd/yyyy) with which only products before should be processed." )
                .create("stop"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("pattern")
                .withDescription("A pattern to search product names on, using '#' as wildcard characters." )
                .create("pattern"));
		options.addOption(OptionBuilder.hasArg(true)
				.withLongOpt("productType")
                .withDescription("The product type on which to locate products." )
                .create("pt"));
		return options;
	}
	

	@SuppressWarnings("static-access")
	private static Options createVerifyCmdOptions() {
		// verify command
		// java CommandLine verify
		Options options = new Options();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(mainOptions.getOption("verify"));
		optionGroup.setRequired(true);
		options.addOptionGroup(optionGroup);
		options.addOptionGroup(createEntityOptions());
		options.addOption(createIndexOption());
		options.addOption(OptionBuilder.hasArg(false)
				.withLongOpt("locationPolicy")
                .withDescription("Validate references against product type location policy" )
                .create("lp"));
		options.addOption(OptionBuilder.hasArg(false)
				.withLongOpt("checksum")
                .withDescription("When verifying a product, also verify its checksum." )
                .create("cs"));
		options.addOption(OptionBuilder.hasArg(false)
                .withDescription("print verify usage" )
                .create("help"));
		return options;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArchiveProperty.getInstance();		
		run(args);
	}
	
	private static void printDeleteUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", deleteOptions, true);
		System.exit(0);
	}
	
	private static void printHelpUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( " ", helpOptions, true );
		System.exit(0);
	}

	private static void printRelocateUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", relocateOptions, true);
		System.exit(0);
	}
	
	private static void printRollingStoreUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( " ", rollingOptions, true );
		System.exit(0);
	}
	
	//printLocateUsage
	private static void printLocateUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( " ", locateOptions, true );
		System.exit(0);
	}
	
	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( " ", mainOptions, true );
		System.exit(0);
	}
	
	private static void printVerifyUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", verifyOptions, true);
		System.exit(0);
	}
	
	private static void printReassociateUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", reassociateOptions, true);
		System.exit(0);
	}
	
	private static void processCmdLine(String[] args) {
		log.info("processCmdLine: ");
		CommandType cmdType = CommandType.UNKNOWN;
		try {
			cmdType = classifyCommand(args);
			if (cmdType==CommandType.UNKNOWN) {
				System.out.println("Unknown Command...");
				printUsage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			printUsage();
		}
		if (cmdType != CommandType.UNKNOWN) {
			CommandLineParser parser = new BasicParser();
			CommandLine cmdLine = null;
			Command.setArguments(args);
			switch(cmdType) {
			case VERIFY : {
				try {
					cmdLine = parser.parse( verifyOptions, args);
				} catch (ParseException e) {
					printVerifyUsage();
				}
				if (cmdLine.hasOption("help")) printVerifyUsage();
				Command.processVerify(cmdLine);
			} break;
			case DELETE : {
				try {
					cmdLine = parser.parse( deleteOptions, args);
				} catch (ParseException e) {
					printDeleteUsage();
				}
				if (cmdLine.hasOption("help")) printDeleteUsage();
				Command.processDelete(cmdLine);
			} break;
			case RELOCATE : {
				try {
					cmdLine = parser.parse( relocateOptions, args);
				} catch (ParseException e) {
					printRelocateUsage();
				}
				if (cmdLine.hasOption("help")) printRelocateUsage();
				Command.processRelocate(cmdLine);
			} break;
			case REASSOCIATE : {
				try {
					cmdLine = parser.parse( reassociateOptions, args);
				} catch (ParseException e) {
					e.printStackTrace();
					printReassociateUsage();
				}
				if (cmdLine.hasOption("help")) printReassociateUsage();
				Command.processReassociate(cmdLine);
			} break;
			case ROLLING_STORE : {
				try {
					cmdLine = parser.parse( rollingOptions, args);
				} catch (ParseException e) {
					printRollingStoreUsage();
				}
				if (cmdLine.hasOption("help")) printRollingStoreUsage();
				Command.processRollingStore(cmdLine);
			} break;
			case LOCATE : {
				try {
					cmdLine = parser.parse( locateOptions, args);
				} catch (ParseException e) {
					printLocateUsage();
				}
				if (cmdLine.hasOption("help")) printLocateUsage();
				Command.processLocate(cmdLine);
			} break;
			case HELP : {
				try {
					cmdLine = parser.parse( helpOptions, args);
				} catch (ParseException e) {
					
					printHelpUsage();
				}
				if (cmdLine.hasOption("verify")) printVerifyUsage();
				if (cmdLine.hasOption("delete")) printDeleteUsage();
				if (cmdLine.hasOption("relocate")) printRelocateUsage();
				if (cmdLine.hasOption("rolling_store")) printRollingStoreUsage();
				if (cmdLine.hasOption("reassociate")) printReassociateUsage();
				if (cmdLine.hasOption("locate")) printLocateUsage();
				printUsage();
			}
            case UNKNOWN:
               break;
			}
		}
	}

	
	public static void run(String[] args) {
		
		String argString = "";
		for(String a: args)
		argString = argString.concat(" " + a);
		
		log.info("Running archive_tool.sh with the following args:" + argString);
		createOptions();
		processCmdLine(args);
	}
}
