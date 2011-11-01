package fiji;

/**
 * A classloader whose classpath can be augmented after instantiation
 */

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderPlus extends URLClassLoader {
	public static ClassLoaderPlus getInFijiDirectory(String... relativePaths) {
		try {
			File directory = new File(getFijiDir());
			URL[] urls = new URL[relativePaths.length];
			for (int i = 0; i < urls.length; i++)
				urls[i] = new File(directory, relativePaths[i]).toURI().toURL();
			return get(urls);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Uh oh: " + e.getMessage());
		}
	}

	public static ClassLoaderPlus get(File... files) {
		try {
			URL[] urls = new URL[files.length];
			for (int i = 0; i < urls.length; i++)
				urls[i] = files[i].toURI().toURL();
			return get(urls);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Uh oh: " + e.getMessage());
		}
	}

	public static ClassLoaderPlus get(URL... urls) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader instanceof ClassLoaderPlus) {
			ClassLoaderPlus classLoaderPlus = (ClassLoaderPlus)classLoader;
			for (URL url : urls)
				classLoaderPlus.add(url);
			return classLoaderPlus;
		}
		return new ClassLoaderPlus(urls);
	}

	public ClassLoaderPlus() {
		this(new URL[0]);
	}

	public ClassLoaderPlus(URL... urls) {
		super(urls, Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(this);
	}

	public void addInFijiDirectory(String relativePath) {
		try {
			add(new File(getFijiDir(), relativePath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Uh oh: " + e.getMessage());
		}
	}

	public void add(String path) throws MalformedURLException {
		add(new File(path));
	}

	public void add(File file) throws MalformedURLException {
		add(file.toURI().toURL());
	}

	public void add(URL url) {
		addURL(url);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName()).append("(");
		for (URL url : getURLs())
			builder.append(" ").append(url.toString());
		builder.append(" )");
		return builder.toString();
	}

	public static String getFijiDir() throws ClassNotFoundException {
		String path = System.getProperty("fiji.dir");
		if (path != null)
			return path;
		final String prefix = "file:";
		final String suffix = "/jars/Fiji.jar!/fiji/ClassLoaderPlus.class";
		path = Class.forName("fiji.ClassLoaderPlus")
			.getResource("ClassLoaderPlus.class").getPath();
		if (path.startsWith(prefix))
			path = path.substring(prefix.length());
		if (path.endsWith(suffix))
			path = path.substring(0,
				path.length() - suffix.length());
		return path;
	}
}