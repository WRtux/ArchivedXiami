package fxiami;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;

import fxiami.entry.AlbumEntry;
import fxiami.entry.ArtistEntry;
import fxiami.entry.MappedEntry;
import fxiami.entry.SongEntry;

/* This file is just a template.
 * Implement your own format and make it work with the web interface.
 */
public final class Indexer {
	
	static void writeEntryIndex(MappedEntry en, DataOutput out) throws IOException {
		//TODO
	}
	
	public static void exportIndex(File dest) throws IOException {
		RandomAccessFile rf = new RandomAccessFile(dest, "rw");
		try {
			System.out.println("Building index...");
			rf.writeInt(0xDEAD494A);
			long off, cur;
			@SuppressWarnings("unchecked")
			Collection<? extends MappedEntry>[] enCols = new Collection[] {
				ArtistEntry.getAll(), AlbumEntry.getAll(), SongEntry.getAll()};
			rf.writeInt(0x584D00F0);
			rf.writeLong(0L);
			off = rf.getFilePointer();
			for (Collection<? extends MappedEntry> col : enCols) {
				for (MappedEntry en : col) {
					writeEntryIndex(en, rf);
				}
			}
			cur = rf.getFilePointer();
			rf.seek(off - 8);
			rf.writeLong(cur - off);
			rf.seek(cur);
			System.out.println("Exporting main...");
			rf.writeInt(0x584D0080);
			rf.writeLong(0L);
			off = rf.getFilePointer();
			for (Collection<? extends MappedEntry> col : enCols) {
				for (MappedEntry en : col) {
					//TODO
				}
			}
			cur = rf.getFilePointer();
			rf.seek(off - 8);
			rf.writeLong(cur - off);
			System.out.println("Indexing...");
			for (Collection<? extends MappedEntry> col : enCols) {
				for (MappedEntry en : col) {
					//TODO
				}
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
