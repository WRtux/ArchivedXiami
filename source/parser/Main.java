import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;

public final class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2)
			throw new IllegalArgumentException();
		export(args[0], new File(args[1]));
	}
	
	public static void export(String typ, File dir) throws IOException {
		File[] fs = dir.listFiles();
		Writer wtr = new OutputStreamWriter(new FileOutputStream(typ + ".jsonm"), "UTF-8");
		for (File f : fs) {
			System.out.println("Process " + f.getName());
			InputStream in = new FileInputStream(f);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String ln = null;
			while ((ln = rdr.readLine()) != null) {
				int i = ln.indexOf("window.__PRELOADED_STATE__");
				if (i == -1)
					continue;
				i = ln.indexOf('"', i) + 1;
				int j = ln.indexOf('"', i);
				if (j == -1)
					continue;
				String dat = ln.substring(i, j);
				dat = new String(IOUtils.decodeBase64(dat), "UTF-8");
				try {
					JSONObject o = JSON.parseObject(dat).getJSONObject(typ + "Data");
					o.put("update", f.lastModified());
					wtr.write(o.toJSONString());
					wtr.write("\r\n");
				} catch (RuntimeException ex) {}
			}
			rdr.close();
		}
		wtr.close();
	}
	
}
