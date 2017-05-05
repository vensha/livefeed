package vensha.livefeed.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class NetUtil {
public static final String	HTTP_EOL	= "\r\n";

private NetUtil() {
}



public static String getIPAddress() throws UnknownHostException {
	InetAddress address = InetAddress.getLocalHost();
	return address.getHostAddress();
}
public static String getPublicIPAddress() throws Exception {
	return NetUtil.fetchWebPage("http://checkip.amazonaws.com/");
}

public static String fetchWebPage(String uadd) throws Exception {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	HttpGet httpGet = new HttpGet(uadd);
	CloseableHttpResponse response = httpclient.execute(httpGet);
	try {
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	} finally {
		response.close();
	}

}

public static InputStream fetchWebStream(String uadd) throws Exception {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	HttpGet httpGet = new HttpGet(uadd);
	CloseableHttpResponse response = httpclient.execute(httpGet);

	HttpEntity entity = response.getEntity();
	return entity.getContent();

}

}