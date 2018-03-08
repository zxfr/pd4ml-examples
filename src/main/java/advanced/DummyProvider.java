package advanced;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.pd4ml.ResourceProvider;
import com.pd4ml.cache.FileCache;

public class DummyProvider extends ResourceProvider {

	public final static String PROTOCOL = "dummy";
	
	@Override
	public BufferedInputStream getResourceAsStream(String resource, FileCache cache) throws IOException {
		if (!resource.toLowerCase().startsWith(PROTOCOL)) {
			return null;
		}
		
		// interpret the "resource" parameter according to your protocol (e.g. as a key to a database record etc)
		
		// in the example we simply dump the resource parameter string
		String buf = "[" + resource.substring(PROTOCOL.length()+1) + "]";
		ByteArrayInputStream baos = new ByteArrayInputStream(buf.getBytes());
		return new BufferedInputStream(baos);
	}

	@Override
	public boolean canLoad(String resource, FileCache cache) {
		if (resource.toLowerCase().startsWith(PROTOCOL)) {
			return true;
		}
		return false;
	}
}