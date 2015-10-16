package aima.gui.applications.search.map;

import java.io.File;

public class Utils {
	
	/*
	 *	Retorna o nome dos arquivos de mapa na pasta /maps ou no diretório passado como parâmetro.
	 *	
	 *	TODO - FileFilter.
	 */
	public static String[] listMapFiles(String filepath) {
		if(filepath == null) {
			System.out.println("[INFO] Using current exection directory.");
			filepath = System.getProperty("user.dir") + "/maps";
		}
		System.out.println("Filepath = " + filepath);
		File folder = new File(filepath);
		File[] dirFiles = folder.listFiles();
		String[] maps = new String[dirFiles.length];
		for(int i = 0; i < dirFiles.length; i++) {
			maps[i] = dirFiles[i].getName();
		}
		return maps;
	}
}
