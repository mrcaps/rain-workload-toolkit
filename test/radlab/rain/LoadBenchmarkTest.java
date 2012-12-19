package radlab.rain;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mrcaps.JSONTweaker;

import radlab.rain.util.ConfigUtil;

public class LoadBenchmarkTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		setOverrides();
		
		StringBuffer configData = new StringBuffer();
		
		String filename = "config/rain.config.olio.json";
		JSONObject jsonConfig = null;
		
		String fileContents = "";
		// Try to load the config file as a resource first
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream( filename );
		if( in != null )
		{
			System.out.println( "[BENCHMARK] Reading config file from resource stream." );
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			String line = "";
			// Read in the entire file and append to the string buffer
			while( ( line = reader.readLine() ) != null )
				configData.append( line );
			fileContents = configData.toString();
		}
		else
		{
			System.out.println( "[BENCHMARK] Reading config file from file system." );
			fileContents = ConfigUtil.readFileAsString( filename );
		}
		
		jsonConfig = new JSONObject( fileContents );
		
		JSONTweaker.overrideJSON(System.getProperties(), "R", jsonConfig);
		
		dumpJSON("Config", jsonConfig);
		Scenario scenario = new Scenario(jsonConfig, true);
	}
	
	public void setOverrides() {
		System.setProperty("Rtiming.rampUp", "5");
		System.setProperty("Rcloudstone-001.target.hostname", "\"1.1.1.1\"");
		System.setProperty("Rcloudstone-001.loadProfile[0].users", "200");
	}
	
	private void dumpJSON(String name, JSONObject obj) throws JSONException {
		System.out.println(name + ":");
		System.out.println(obj.toString(2));
	}
}
