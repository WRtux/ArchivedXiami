package fxiami;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.MappedEntry;
import fxiami.entry.EntryPort;
import fxiami.entry.SongEntry;

public final class Loader {
	
	public static List<MappedEntry> loadJSON(String typ, File f) throws IOException {
		if (MappedEntry.getEntryClass(typ) == null)
			throw new IllegalArgumentException("Unknown entry type.");
		List<MappedEntry> li = new ArrayList<>();
		InputStream in = new FileInputStream(f);
		JSONReader rdr = new JSONReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
		try {
			System.out.println("Loading...");
			rdr.startArray();
			while (rdr.hasNext()) {
				try {
					li.add((MappedEntry)EntryPort.parseJSON(typ, (JSONObject)rdr.readObject()));
				} catch (RuntimeException ex) {
					System.err.println("Unexpected break:");
					ex.printStackTrace();
				}
			}
			rdr.endArray();
			System.out.println("Load complete.");
		} catch (Exception ex) {
			System.err.println("Load failed.");
			throw ex;
		} finally {
			rdr.close();
			in.close();
		}
		return li;
	}
	
	static void writeArray(Collection<? extends MappedEntry> co, JSONWriter wtr) {
		wtr.startArray();
		for (MappedEntry en : co) {
			try {
				wtr.writeValue(en.toJSON());
			} catch (RuntimeException ex) {
				System.err.println("Unexpected break:");
				ex.printStackTrace();
			}
		}
		wtr.endArray();
	}
	
	public static void exportJSON(Collection<? extends MappedEntry> co, File dest) throws IOException {
		OutputStream out = new FileOutputStream(dest);
		JSONWriter wtr = new JSONWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
		wtr.config(SerializerFeature.WriteMapNullValue, true);
		try {
			System.out.println("Exporting...");
			writeArray(co, wtr);
			System.out.println("Export complete.");
		} catch (Exception ex) {
			System.err.println("Export failed.");
			throw ex;
		} finally {
			wtr.flush();
			out.close();
			System.gc();
		}
	}
	public static void exportJSON(String typ, File dest) throws IOException {
		exportJSON(MappedEntry.getAll(typ), dest);
	}
	
	public static void exportJSON(File dest) throws IOException {
		OutputStream out = new FileOutputStream(dest);
		JSONWriter wtr = new JSONWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
		wtr.config(SerializerFeature.WriteMapNullValue, true);
		try {
			System.out.println("Exporting...");
			wtr.startObject();
			wtr.writeKey("artists");
			writeArray(ArtistEntry.getAll(), wtr);
			wtr.writeKey("albums");
			writeArray(AlbumEntry.getAll(), wtr);
			wtr.writeKey("songs");
			writeArray(SongEntry.getAll(), wtr);
			wtr.endObject();
			System.out.println("Export complete.");
		} catch (Exception ex) {
			System.err.println("Export failed.");
			throw ex;
		} finally {
			wtr.flush();
			out.close();
			System.gc();
		}
	}
	
	@Deprecated
	private Loader() {
		throw new IllegalStateException();
	}
	
}
