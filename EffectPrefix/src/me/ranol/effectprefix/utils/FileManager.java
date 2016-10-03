package me.ranol.effectprefix.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class FileManager {
	public static boolean copy(File fOrg, File fTarget) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel fci;
		FileChannel fco;
		try {
			fis = new FileInputStream(fOrg);

			if (!fTarget.isFile()) {
				File fParent = new File(fTarget.getParent());
				if (!fParent.exists()) {
					fParent.mkdir();
				}
				fTarget.createNewFile();
			}

			fos = new FileOutputStream(fTarget);
			fci = fis.getChannel();
			fco = fos.getChannel();

			long size = fci.size();
			fci.transferTo(0, size, fco);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean download(String url, File downloaded) {
		try {
			URL web = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(web.openStream());
			FileOutputStream fos = new FileOutputStream(downloaded);
			fos.getChannel().transferFrom(rbc, 0L, 9223372036854775807L);
			fos.close();
			rbc.close();
			return true;
		} catch (Exception except) {
		}
		return false;
	}
}