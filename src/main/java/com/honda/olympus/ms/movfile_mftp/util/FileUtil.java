package com.honda.olympus.ms.movfile_mftp.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class FileUtil 
{
	
	private static final String SLASH = "/";	
	
	
	private FileUtil() { }
	
	
	public static String concat(String baseDir, String fileName) {
		return baseDir + SLASH  + fileName; 
	}
	
	
	public static String fixSlashes(String path) {
		return path.replace("\\", "/").replaceAll("/{2,}", "/");
	}
	
	
	public static void createDir(String dir) {
		Path path = Paths.get(dir);
		try {
			if (!path.toFile().exists()) { 
				log.info("# Creating dir '{}' ...", path.getFileName());
				path.toFile().mkdirs();
				return;
			}
			log.info("# Dir '{}' already exists !", path.getFileName());
		}
		catch (SecurityException se) {
			log.error("### Error found while accessing '{}'", path.getFileName());
			throw se;
		}
	}
	
	
	public static void removeFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			path.toFile().delete();
			log.info("# Dir '{}' deleted !", path.getFileName());
		}
		catch(Exception e) {
			log.error("### Error found while deleting '{}'", path.getFileName(), e);
		}
	}

}
