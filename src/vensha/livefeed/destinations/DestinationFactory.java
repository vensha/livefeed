package vensha.livefeed.destinations;



import java.util.Properties;

import vensha.livefeed.EntityManager;
import vensha.utils.LogManager;

public class DestinationFactory {
private static Properties props_ = null;
private static boolean useEchoDestination_=false; //

public static void init(Properties props){
	props_ = props;
	String p = props_.getProperty("use.echo.destination", "false");
	if (p.equalsIgnoreCase("true")) {
		useEchoDestination_ = true;
		LogManager.log("Using EchoDestination");
	}
	
}

public static Destination getDestination(String dtype) {
	if (useEchoDestination_) {
		return new EchoDestination();
	}
		
	String type = dtype.toLowerCase();
	switch (type) {
		case "echo":
			return new EchoDestination();
		default:
			String[] values = props_.getProperty(type + ".creds").split(",");
			return new SatoriDestination(values[0], values[1], values[2], values[3],values[4]);
			
	}
	
}


}
