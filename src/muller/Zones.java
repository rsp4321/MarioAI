package muller;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dk.itu.mario.res.ResourcesManager;

/**
 * Class Zones to load Zones from FIles or Directories in the zones array
 * 
 * @author mis09mmh
 */
public class Zones {
	ArrayList<Zone> zones;

	public Zones() {
		zones = new ArrayList<Zone>();
	}

	public Zone get(int i) {
		return zones.get(i);
	}

	public Zone getZone(int i) {
		return zones.get(i);
	}

	public int getNumZones() {
		return zones.size();
	}

	public void loadFromDir(String dir) {

		String files;
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		// System.out.println(" : dir "+folder.getPath());
		// //
		// System.out.println(" : dir Res "+Zones.class.getResource("zones"));
		// System.out.println(" : dir Can "+Zones.class.getCanonicalName());

		// try {
		// System.out.println(" : dir "+folder.getCanonicalPath());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(folder.listFiles()+" : dir");
		// System.out.println("Loading 13");
		// loadFromFile("zones//13.txt");
		// System.out.println(dir +" 13 Loaded");

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				loadFromFile(dir + listOfFiles[i].getName());
				// System.out.println(files);
			}
		}

	}

	private static void findClassesInJar() throws IOException {
		try {
			JarInputStream in = new JarInputStream(Zones.class
					.getProtectionDomain().getCodeSource().getLocation()
					.toURI().toURL().openStream());
			JarEntry entry = in.getNextJarEntry();
			ArrayList<String> entryList = new ArrayList<String>();
			while (entry != null) {
				entryList.add(entry.toString());
				entry = in.getNextJarEntry();
			}
			in.close();

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadFromFile(String filename) {
		// URL fis = Zones.class.getResource(filename);
		// System.out.println("fis:"+fis.getPath());
		Zone test = new Zone();
		File file = new File(filename);
		// System.out.println("fis:"+file.getPath());
		try {
			FileReader fr = new FileReader(file);

			String gelesen;

			char[] temp = new char[(int) file.length()];

			// Lesevorgang
			fr.read(temp);

			gelesen = new String(temp);

			String tmp[] = null;
			String head = new String(temp);
			String functions = new String(temp);
			String matrix = new String(temp);
			tmp = gelesen.split("</head>");
			head = tmp[0];
			matrix = tmp[1];

			ArrayList al = new ArrayList(Arrays.asList(matrix.split("\n")));
			ArrayList al2 = new ArrayList(Arrays.asList(head.split("\r\n")));

			al2.remove(0);
			al2.remove(al2.size() - 1);

			System.out.println(al2.toString());
			String function = new String();
			String str[] = new String[2];
			
			/*
			 * Parsing da seção HEAD
			 */
			for (int i = 0; i < al2.size(); i++) {

				function = al2.get(i).toString();
				str = function.split("=");

				if (str[0].contains("Tube"))
					try {
						test.noTubes = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Plat"))
					try {
						test.noPlattforms = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Coin"))
					try {
						test.noCoins = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Quest"))
					try {
						test.noPowerups = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Gap"))
					try {
						test.noGaps = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Blocks"))
					try {
						test.noBlocks = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("Object"))
					try {
						test.noObject = Integer.parseInt(str[1]);
					} catch (Exception E) {
					}
				else if (str[0].contains("width"))
					try {
						test.setWidth(Integer.parseInt(str[1]));
					} catch (Exception E) {
					}

			}

			al.remove(0);
			al.remove(0);
			al.remove(al.size() - 1);

			ArrayList line = new ArrayList(Arrays.asList(al.get(0).toString()
					.split("\r\n")));
			// sList = al.toArray();

			line = new ArrayList(Arrays.asList(al.get(0).toString().split(",")));
			test.height = al.size();

			test.width = line.size();
			test.matrix = new int[test.height][test.width];

			// test.height=al.size();
			for (int i = 0; i < test.height; i++) {
				// there are some special characters in there, get rid of them
				line = new ArrayList(Arrays.asList(al.get(i).toString()
						.split(",")));
				int tm;
				for (int j = 0; j < line.size(); j++) {
					try {
						String t = line.get(j).toString()
								.replaceAll("[^0-9]", "");
						tm = Integer.parseInt(t);

						if (tm > 128)
							tm = tm - 256;
						if (tm == -127)
							tm = -123;
						if (tm == -111)
							tm = -107;
						test.matrix[i][j] = tm;

						// test.matrix[i][j]=Integer.valueOf(line.get(j).toString()).intValue();
					} catch (Exception ex) // only allow integer values
					{
						System.out.println("NOT A NUMBER:"
								+ line.get(j).toString());
					}
				}

			}

			//
			fr.close();

			zones.add(test);
			System.out.println(zones.size());
		} catch (FileNotFoundException e1) {
			//
			System.err.println("File not found: " + file);
		} catch (IOException e2) {
			//
			e2.printStackTrace();
		}

	}
}
