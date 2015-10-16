package aima.core.environment.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *	Singleton class that handles all maps. 
 */
public class GenericRoadMap extends ExtendableMap {
	
	/*
	 *	 Attrs
	 */
	private ArrayList<String> dsts;
	private ArrayList<String> srcs;
	private String mapName;
	
	/*
	 *	Statics Attrs 
	 */
	private static HashMap<String, GenericRoadMap> instances = new HashMap<String, GenericRoadMap>();
	private static final String USING_COORDINATES = "#coordinates";
	
	/*
	 *	Singleton get instance methods
	 */
	public static GenericRoadMap getInstance(String filepath) {
		return getInstance(filepath, null);
	}
	
	public static GenericRoadMap getInstance(String filepath, ExtendableMap map) {
		if(instances.get(filepath) == null) {
			try {
				if(map == null)
					instances.put(filepath, new GenericRoadMap(filepath));
				else
					instances.put(filepath, new GenericRoadMap(filepath, map));
			} catch(Exception e) {
				System.out.println(">> Exception message = " + e.getMessage());
				return null;
			}
		}
		
		return instances.get(filepath);
	}
	
	
	
	/*
	 * 	Getters
	 */
	public ArrayList<String> getDsts() {
		return dsts;
	}
	
	public ArrayList<String> getSrcs() {
		return srcs;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	/*
	 *	Private constructor 
	 */
	private GenericRoadMap(String filepath) throws Exception {
		dsts = new ArrayList<String>();
		srcs = new ArrayList<String>();
		initMap(this, filepath);
	}
	
	private GenericRoadMap(String filepath, ExtendableMap map) throws Exception {
		super(map); // initializes an
		dsts = new ArrayList<String>();
		srcs = new ArrayList<String>();
		initMap(this, filepath);
	}
	
	/*
	 *	Helpers 
	 */
	private static void initMap(GenericRoadMap map, String filepath) throws Exception {
		if(filepath == null || filepath.equals("")) {
			throw new Exception("[ERROR] No map provided");
		}
		String completeFilepath = System.getProperty("user.dir") + "/maps/" + filepath;
		BufferedReader br = new BufferedReader(new FileReader(completeFilepath));
		try {
			String buildStrategy = br.readLine();
			boolean useCoordinates = buildStrategy.equals(USING_COORDINATES) ? true : false; 
			map.mapName = br.readLine();
			System.out.println("MAP NAME = " + map.mapName);
			int nNodes = Integer.parseInt(br.readLine()); // number of map nodes
			for(int i = 0; i < nNodes; i++){ // for each node, get the name, distance and direction from ref location
				String line = br.readLine();
				System.out.println(">> " + line);
				String[] splitted = line.split(" ");
				if(useCoordinates) {
					map.setPosition(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
				} else {
					map.setDistAndDirToRefLocation(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
                }
                map.dsts.add(splitted[0]);
                map.srcs.add(splitted[0]);
			}
			
			int nRoutes = Integer.parseInt(br.readLine()); //number of routes
			for(int i = 0; i < nRoutes; i++) {
				String line = br.readLine();
				System.out.println(">> " + line);
				String[] splitted = line.split(" ");
				map.addBidirectionalLink(splitted[0], splitted[1], Double.parseDouble(splitted[2]));
			}
			
		} finally {
			br.close();
			System.out.println(">> Finished parsing file");
		}
	}
}
