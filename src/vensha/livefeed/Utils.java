package vensha.livefeed;

import java.util.Properties;

public class Utils {
private static Properties cfg_;

public static void setConfig(Properties cfg) {
	cfg_ = cfg;
}

public static Properties getConfig() {
	return cfg_;
}

}
