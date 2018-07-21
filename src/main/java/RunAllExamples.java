import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RunAllExamples {
	
	private static String[] packages = {
			"basics",
			"html",
			"i18n",
			"advanced",
			"pdftools",
			};
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		List<File> before = listTempDir();
		for ( String pkg : packages ) {
			File packagedir = new File("src/main/java/" + pkg);
			packagedir.mkdirs();
			System.out.println("\n" + packagedir.getAbsolutePath() + " already exists or created.");
			Class[] classes = getClasses(pkg);
			for ( Class cls : classes ) {
				System.out.println();
				try {
					java.lang.reflect.Method method = cls.getMethod("main", String[].class);
					final Object[] args2 = new Object[1];
					args2[0] = new String[] {"noviewer"};
					method.invoke(null, args2);
					List<File> after = listTempDir();
					File newOne = compareContent(before, after);
					if (newOne != null)  {
						File target = new File("src/main/java/" + cls.getName().replace('.', '/') + ".pdf" );
						if ( target.exists() ) {
							target.delete();
						}
						copyFile(newOne, target);
						newOne.delete();
						System.out.println("EXAMPLE: " + cls.getName() + " -> " + target.getAbsolutePath());
					}
					before = after;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static File compareContent(List<File> before, List<File> after) {
		for ( File name : after ) {
			if ( !before.contains(name) ) {
				return name;
			}
		}
		return null;
	}

	private static List<File> listTempDir() {
		List<File> results = new ArrayList<File>();
		File[] files = new File(System.getProperty("java.io.tmpdir")).listFiles();
		for (File file : files) {
		    if (file.isFile() && file.getName().endsWith(".pdf") ) {
		        results.add(file);
		    }
		}	
		return results;
	}

	private static Class[] getClasses(String packageName)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class> classes = new ArrayList<Class>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        try {
				if (file.isDirectory()) {
				    assert !file.getName().contains(".");
				    classes.addAll(findClasses(file, packageName + "." + file.getName()));
				} else if (file.getName().endsWith(".class") && 
						Integer.parseInt(file.getName().substring(1, 4)) >= 0 &&
						file.getName().indexOf('$') < 0) {
				    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				}
			} catch (NumberFormatException e) {
			}
	    }
	    return classes;
	}
	
	private static void copyFile(File source, File dest) throws IOException {
	    Files.copy(source.toPath(), dest.toPath());
	}
}
