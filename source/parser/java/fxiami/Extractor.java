package fxiami;

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

public final class Extractor {
	
	public static String convertRaw(String typ, File f) throws IOException {
		InputStream in = new FileInputStream(f);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		try {
			String ln = null;
			while ((ln = rdr.readLine()) != null) {
				int i = ln.indexOf("window.__PRELOADED_STATE__"), j;
				if (i == -1)
					continue;
				i = ln.indexOf('"', i) + 1;
				j = ln.indexOf('"', i);
				if (j == -1)
					continue;
				String dat = ln.substring(i, j);
				dat = new String(IOUtils.decodeBase64(dat), "UTF-8");
				try {
					JSONObject o = JSON.parseObject(dat).getJSONObject(typ + "Data");
					o.put("update", f.lastModified());
					return o.toJSONString();
				} catch (RuntimeException ex) {}
			}
			return null;
		} finally {
			rdr.close();
		}
	}
	
	public static void extractRaw(String typ, File[] src, File dest) throws IOException {
		Writer wtr = new OutputStreamWriter(new FileOutputStream(dest), "UTF-8");
		try {
			System.out.println("Extracting " + src.length + " files...");
			for (File f : src) {
				System.out.println("Processing " + f.getName() + "...");
				String str = convertRaw(typ, f);
				if (str != null) {
					wtr.write(str);
					wtr.write("\r\n");
				} else {
					System.out.println("No data found.");
				}
			}
			System.out.println("Extract complete.");
		} catch (Exception ex) {
			System.err.println("Extract failed.");
			throw ex;
		} finally {
			wtr.close();
		}
	}
	
	@Deprecated
	private Extractor() {
		throw new IllegalStateException();
	}
	
}
