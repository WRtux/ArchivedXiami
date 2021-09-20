package fxiami;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.EntryPort;
import fxiami.entry.MappedEntry;
import fxiami.entry.Helper;
import fxiami.entry.ReferenceEntry;
import fxiami.entry.SongEntry;

public final class Indexer {
	
	static void writeEntryIndex(MappedEntry en, String[] exts, DataOutput out) throws IOException {
		out.writeInt(0);
		out.writeInt(0);
		out.writeInt(en.id != null ? en.id.intValue() : 0xFFFFFFFF);
		out.writeUTF(en.sid != null ? en.sid : "");
		out.writeUTF(en.name != null && en.name != EntryPort.NULL_STRING ? en.name : "");
		if (exts != null) {
			out.writeShort(exts.length);
			for (String str : exts) {
				out.writeUTF(str);
			}
		} else {
			out.writeShort(0);
		}
	}
	
	public static void exportIndex(File dest) throws IOException {
		RandomAccessFile rf = new RandomAccessFile(dest, "rw");
		try {
			System.out.println("Indexing...");
			long off, cur;
			Map<MappedEntry, long[]> mapOff = new LinkedHashMap<>();
			rf.writeInt(0xFE581A4D);
			off = rf.getFilePointer() + 4;
			rf.seek(off);
			for (ArtistEntry en : ArtistEntry.getAll()) {
				String[] exts = null;
				if (en.subName != null && en.subName != EntryPort.NULL_STRING)
					exts = new String[] {en.subName};
				rf.writeByte(0x10);
				mapOff.put(en, new long[] {rf.getFilePointer(), 0L, 0L});
				writeEntryIndex(en, exts, rf);
			}
			for (AlbumEntry en : AlbumEntry.getAll()) {
				Set<String> set = new HashSet<>();
				if (en.subName != null && en.subName != EntryPort.NULL_STRING)
					set.add(en.subName);
				if (en.artists != null && !Helper.isNullArray(en.artists)) {
					for (ReferenceEntry ren : en.artists) {
						if (ren != null && ren.name != null && ren.name != EntryPort.NULL_STRING)
							set.add(ren.name);
					}
				}
				rf.writeByte(0x11);
				mapOff.put(en, new long[] {rf.getFilePointer(), 0L, 0L});
				writeEntryIndex(en, set.toArray(new String[0]), rf);
			}
			for (SongEntry en : SongEntry.getAll()) {
				Set<String> set = new HashSet<>();
				if (en.subName != null && en.subName != EntryPort.NULL_STRING)
					set.add(en.subName);
				if (en.translation != null && en.translation != EntryPort.NULL_STRING)
					set.add(en.translation);
				if (en.artist != null && en.artist.name != null && en.artist.name != EntryPort.NULL_STRING)
					set.add(en.artist.name);
				if (en.singers != null && !Helper.isNullArray(en.singers)) {
					for (ReferenceEntry ren : en.singers) {
						if (ren != null && ren.name != null && ren.name != EntryPort.NULL_STRING)
							set.add(ren.name);
					}
				}
				if (en.album != null && en.album.name != null && en.album.name != EntryPort.NULL_STRING)
					set.add(en.album.name);
				rf.writeByte(0x12);
				mapOff.put(en, new long[] {rf.getFilePointer(), 0L, 0L});
				writeEntryIndex(en, set.toArray(new String[0]), rf);
			}
			rf.writeByte(0x00);
			cur = rf.getFilePointer();
			rf.seek(off - 4);
			rf.writeInt((int)(cur - off));
			rf.seek(cur);
			rf.writeInt(0xF0581A4D);
			off = rf.getFilePointer() + 4;
			rf.seek(off);
			for (ArtistEntry en : ArtistEntry.getAll()) {
				long[] offs = mapOff.get(en);
				offs[1] = rf.getFilePointer() - off;
				rf.write(en.toJSON().toJSONString().getBytes("UTF-8"));
				offs[2] = rf.getFilePointer() - off - offs[1];
				rf.writeByte(0x1E);
			}
			rf.writeByte(0x1D);
			for (AlbumEntry en : AlbumEntry.getAll()) {
				long[] offs = mapOff.get(en);
				offs[1] = rf.getFilePointer() - off;
				rf.write(en.toJSON().toJSONString().getBytes("UTF-8"));
				offs[2] = rf.getFilePointer() - off - offs[1];
				rf.writeByte(0x1E);
			}
			rf.writeByte(0x1D);
			for (SongEntry en : SongEntry.getAll()) {
				long[] offs = mapOff.get(en);
				offs[1] = rf.getFilePointer() - off;
				rf.write(en.toJSON().toJSONString().getBytes("UTF-8"));
				offs[2] = rf.getFilePointer() - off - offs[1];
				rf.writeByte(0x1E);
			}
			rf.writeByte(0x1C);
			cur = rf.getFilePointer();
			rf.seek(off - 4);
			rf.writeInt((int)(cur - off));
			for (long[] offs : mapOff.values()) {
				rf.seek(offs[0]);
				rf.writeInt((int)offs[1]);
				rf.writeInt((int)offs[2]);
			}
			System.out.println("Index complete.");
		} catch (Exception ex) {
			System.err.println("Index failed.");
			throw ex;
		} finally {
			rf.close();
			System.gc();
		}
	}
	
	@Deprecated
	private Indexer() {
		throw new IllegalStateException();
	}
	
}
