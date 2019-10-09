package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Note;


public class DirectoryMonitor
{
	// Variables
	public static final String defaultDir = "./pages";
	private WatchService watcher;
	private Map<WatchKey, Path> monitored;
	private List<Path> files;
	
	
	// Constructor - takes the directory to be monitored.
	// This directory can not be changed after the fact.
	public DirectoryMonitor(Path directoryToMonitor)
	{
		try
		{
			this.watcher = FileSystems.getDefault().newWatchService();
			this.monitored = new HashMap<WatchKey, Path>();
			this.files = new ArrayList<Path>();
			
			// Set up existing files
			readExistingFiles(directoryToMonitor);
			
			WatchKey key = directoryToMonitor.register(watcher, 
					StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
				
			monitored.put(key, directoryToMonitor);
			
			Note.Log("Monitor started for " + directoryToMonitor);
		}
		catch(Exception e)
		{
			Note.Error("Directory monitor error: " + e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public boolean update() 
	{
		boolean updated = false;
		
		try
		{
			WatchKey key = watcher.take();
			Path dir = monitored.get(key);
			
			if (dir == null)
			{
				Note.Warn("WatchKey was not found");
				return false;
			}
			
			for (WatchEvent<?> event : key.pollEvents())
			{
				WatchEvent.Kind kind = event.kind();
				Path name = ((WatchEvent<Path>)event).context();
				Path child = dir.resolve(name);
				
				if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(child))
				{
					registerDirectory(child);
					Note.Log("Found a child directory: " + child);
				}
				else
				{
					updated = true;
					Note.Log("Event " + event.kind() + " for file " + child);
				}
				
				// Remove the key and continue
				if (!key.reset())
				{
					monitored.remove(key);
					
					if (monitored.isEmpty())
					{
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			Note.Error("Issue while monitoring: " + e);
		}
		
		return updated;
	}
	
	// Tracks a given directory
	private void registerDirectory(Path directoryPath)
	{
		try
		{
			Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					WatchKey key = dir.register(watcher, 
							StandardWatchEventKinds.ENTRY_CREATE, 
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);
						
					monitored.put(key, dir);
					
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Performs an initial registration for files
	private void readExistingFiles(Path directoryPath)
	{
		File folder = new File(directoryPath.toString());
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				files.add(Paths.get(listOfFiles[i].getPath()));
			}
			else if (listOfFiles[i].isDirectory())
			{
				Note.Log("Sub-Directory skipped: " + listOfFiles[i].getName());
			}
		}
	}
	
	// Takes the known existing files and parses them into a map that maps the
	// name of the file (with extension) the contents inside it.
	public Map<String, String> getFileContents()
	{
		Map<String, String> map = new HashMap<String, String>();
		
		try
		{
			for(Path path : files)
			{
				String contents = new String(Files.readAllBytes(path));
				map.put(path.getFileName().toString(), contents);
			}
		}
		catch (IOException e)
		{
			Note.Error("Exception during content reading " + e);
		}
		return map;
	}
}
