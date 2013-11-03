package com.compendium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper of ClassLoader.getSystemClassLoader().
 */
class LanguageClassLoader extends ClassLoader {
	
	static final Logger log = LoggerFactory.getLogger(LanguageClassLoader.class);

    public LanguageClassLoader(){
        super(LanguageClassLoader.class.getClassLoader());
    }
 	
	private final ClassLoader loader = ClassLoader.getSystemClassLoader(); 

	public InputStream getResourceAsStream(String name) {		
		String fullPath = findLanguageFile(name);
		
		if (!fullPath.equals("")) {				
			try {
				File file = new File(fullPath);		
				return new FileInputStream(file);
			} catch (Exception e) {
				log.error("Exception...", e);
			}

			if (loader != null) {
				return loader.getResourceAsStream(fullPath);
			}
	    }
	    
		return ClassLoader.getSystemResourceAsStream(name);
	}
	
	private String findLanguageFile(String name) {
		String fullPath = null;
		
		File startingDirectory= new File(ProjectCompendium.DIR_LOCALE);
	    try {
	    	List<File> files = getFileListing(startingDirectory);
			for (int i=0; i<files.size();i++) {
				File file = files.get(i);
				if (file.getName().equals(name)) {
					if (loader != null) {
						fullPath = (file.getPath()).replace('\\','/');
					}					
					break;
				}
			}
	    } catch(IOException e) {
	    	log.error("Exception...", e);
	    } 
	    
		return fullPath;
	}
	  
	/**
	 * Recursively walk a directory tree and return a List of all
	 * Files found; the List is sorted using File.compareTo().
	 *
	 * @param aStartingDir is a valid directory, which can be read.
	 */
	private List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
		  validateDirectory(aStartingDir);
		  List<File> result = getFileListingNoSort(aStartingDir);
		  //Collections.sort(result);
		  return result;
	  }
	
	  private List<File> getFileListingNoSort(File aStartingDir) throws FileNotFoundException {
		  List<File> result = new ArrayList<File>();
		  File[] filesAndDirs = aStartingDir.listFiles();
		  List<File> filesDirs = Arrays.asList(filesAndDirs);
		  for(File file : filesDirs) {
			  result.add(file); //always add, even if directory
			  if ( ! file.isFile() ) {
				  //must be a directory
				  //recursive call!
				  List<File> deeperList = getFileListingNoSort(file);
				  result.addAll(deeperList);
			  }
		  }
		  return result;
	  }
	
	  /**
	   * Directory is valid if it exists, does not represent a file, and can be read.
	   */
	  private void validateDirectory (File aDirectory) throws FileNotFoundException {
		  if (aDirectory == null) {
			  throw new IllegalArgumentException("Directory should not be null.");
		  }
		  if (!aDirectory.exists()) {
			  throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		  }
		  if (!aDirectory.isDirectory()) {
			  throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		  }
		  if (!aDirectory.canRead()) {
			  throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		  }		
	 }
}
