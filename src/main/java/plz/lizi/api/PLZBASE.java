package plz.lizi.api;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import plz.lizi.api.superbyte.ClassPool;
import plz.lizi.api.superbyte.CtClass;
import plz.lizi.api.superbyte.bytecode.AnnotationsAttribute;
import plz.lizi.api.superbyte.bytecode.annotation.Annotation;
import plz.lizi.api.superbyte.bytecode.annotation.ArrayMemberValue;
import plz.lizi.api.superbyte.bytecode.annotation.ClassMemberValue;
import plz.lizi.api.superbyte.bytecode.annotation.MemberValue;
import sun.misc.Unsafe;

public class PLZBASE {
	public static final Unsafe UNSAFE = getUnsafe();
	public static final MethodHandles.Lookup LOOKUP = getLookup();
	public static Scanner scanner = new Scanner(System.in);

	public static Unsafe getUnsafe() {
		if (UNSAFE != null)
			return UNSAFE;
		Unsafe instance = null;
		try {
			Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor();
			c.setAccessible(true);
			instance = c.newInstance();
		} catch (Throwable var3) {
			var3.printStackTrace();
		}
		return instance;
	}

	public static MethodHandles.Lookup getLookup() {
		try {
			return (MethodHandles.Lookup) UNSAFE.getObjectVolatile(UNSAFE.staticFieldBase(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP")), UNSAFE.staticFieldOffset(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP")));
		} catch (Exception e) {
			try {
				Constructor<MethodHandles.Lookup> c = MethodHandles.Lookup.class.getDeclaredConstructor();
				c.setAccessible(true);
				return c.newInstance();
			} catch (Throwable var3) {
				var3.printStackTrace();
			}
		}
		return null;
	}

	public static void extractJar(String jarPath, String destDir) throws IOException {
		File destDirectory = new File(destDir);
		if (!destDirectory.exists()) {
			destDirectory.mkdirs();
		}
		try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarPath))) {
			JarEntry entry;
			while ((entry = jarInputStream.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				File entryFile = new File(destDir, entryName);
				if (entry.isDirectory()) {
					entryFile.mkdirs();
					continue;
				}
				File parent = entryFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				try (OutputStream outputStream = new FileOutputStream(entryFile)) {
					byte[] buffer = readAllBytes(jarInputStream);
					outputStream.write(buffer);
				}
			}
		}
	}

	public static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, bytesRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

	public static byte[] getClassBytes(String jarPath, String className) throws Exception {
		try (JarFile jarFile = new JarFile(jarPath)) {
			String classPath = className.replace('.', '/') + ".class";
			JarEntry entry = jarFile.getJarEntry(classPath);
			if (entry == null) {
				jarFile.close();
				throw new ClassNotFoundException("Class not found in JAR: " + className);
			}
			try (InputStream is = jarFile.getInputStream(entry)) {
				return readAllBytes(is);
			}
		}
	}

	public static byte[] getClassBytes(Class<?> clazz) throws Exception {
		InputStream is = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
		byte[] dat = new byte[is.available()];
		is.read(dat);
		is.close();
		return dat;
	}

	public static String getJarPath() {
		try {
			CodeSource codeSource = PLZBASE.class.getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				URL jarUrl = codeSource.getLocation();
				return new File(jarUrl.getPath()).getAbsolutePath().split("%")[0];
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getJarPath(Class<?> cls) {
		try {
			CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				return new File(codeSource.getLocation().getPath()).getAbsolutePath().split("%")[0];
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List<String> getClassNamesFromJar(String jarPath) {
		List<String> classNames = new ArrayList<>();
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".class")) {
					String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
					classNames.add(className);
				}
			}
			jarFile.close();
		} catch (Throwable t) {
		}
		return classNames;
	}

	public static Map<Object, Object> getManifestAttributes(String jarPath) {
		try (JarFile jarFile = new JarFile(jarPath)) {
			Manifest manifest = jarFile.getManifest();
			return (manifest != null) ? manifest.getMainAttributes() : null;
		} catch (IOException e) {
			return new HashMap<>();
		}
	}

	public static List<String> filesInFolder(String folderPath, String extension) {
		List<String> filePaths = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return filePaths;
		}
		String suffix = extension.startsWith(".") ? extension : "." + extension;
		suffix = suffix.toLowerCase();
		File[] files = folder.listFiles();
		if (files == null) {
			return filePaths;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				filePaths.addAll(filesInFolder(file.getAbsolutePath(), extension));
			} else {
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(suffix)) {
					filePaths.add(file.getAbsolutePath());
				}
			}
		}
		return filePaths;
	}

	public static String readFile(String filePath) throws Exception {
		Path path = Paths.get(filePath);
		File file = path.toFile();
		if (!file.exists()) {
			throw new RuntimeException("null");
		}
		byte[] encoded = Files.readAllBytes(path);
		return new String(encoded, StandardCharsets.UTF_8);
	}

	public static void writeFile(String filePath, String content) throws Throwable {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(content);
		}
	}

	public static String getStackTrace() {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			builder.append(stackTrace);
			builder.append("\n");
		}
		return builder.toString();
	}

	public static List<String> getAnnotationValues(CtClass ctClass, String annotationType, String annotationMember) {
		List<String> targets = new ArrayList<>();
		AnnotationsAttribute attribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.invisibleTag);
		List<Annotation> annotations = List.of((attribute == null ? new Annotation[] {} : attribute.getAnnotations()));
		for (Annotation annotation : annotations) {
			if (annotationType.equals(annotation.getTypeName())) {
				MemberValue value = annotation.getMemberValue(annotationMember);
				if (value instanceof ArrayMemberValue) {
					ArrayMemberValue arrayValue = (ArrayMemberValue) value;
					MemberValue[] values = arrayValue.getValue();
					for (MemberValue memberVal : values) {
						if (memberVal instanceof ClassMemberValue) {
							String className = ((ClassMemberValue) memberVal).getValue().replace('/', '.');
							targets.add(className);
						}
					}
				}
			}
		}
		ctClass.detach();
		return targets;
	}

	public static void accessModule(Module moudle) {
		try {
			LOOKUP.findStatic(Module.class, "addReads0", MethodType.methodType(void.class, Module.class, Module.class)).invoke(moudle, null);
		} catch (Throwable e) {
		}
	}

	public static List<Module> getAllModules() {
		return List.of((Module[]) PLZAPII.getClassInstances(Module.class));
	}

	public static List<String> findMixTarget(String jarPath, String className) {
		List<String> modifiedClasses = new ArrayList<>();
		try {
			ClassPool pool = new ClassPool();
			pool.insertClassPath(jarPath);
			CtClass ctClass = pool.get(className);
			if (ctClass == null) {
				ctClass = pool.makeClassIfNew(new ByteArrayInputStream(getClassBytes(jarPath, className)));
			}
			final String MIXIN_ANNOTATION = "org.spongepowered.asm.mixin.Mixin";
			AnnotationsAttribute attribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.invisibleTag);
			List<Annotation> annotations = List.of((attribute == null ? new Annotation[] {} : attribute.getAnnotations()));
			for (Object annotationObj : annotations) {
				Annotation annotation = (Annotation) annotationObj;
				if (annotation.getTypeName().equals(MIXIN_ANNOTATION)) {
					MemberValue value = annotation.getMemberValue("value");
					if (value instanceof ClassMemberValue) {
						String className1 = ((ClassMemberValue) value).getValue().replace('/', '.').replace("L", "").replace("; ", "");
						modifiedClasses.add(className1);
					} else if (value instanceof ArrayMemberValue) {
						MemberValue[] values = ((ArrayMemberValue) value).getValue();
						for (MemberValue mv : values) {
							if (mv instanceof ClassMemberValue) {
								String className2 = ((ClassMemberValue) mv).getValue().replace('/', '.').replace("L", "").replace("; ", "");
								modifiedClasses.add(className2);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modifiedClasses;
	}

	public static void println(Object o) {
		System.out.println(o);
	}

	public static void println() {
		System.out.println();
	}

	public static void print(Object o) {
		System.out.print(o);
	}

	public static String input(String s) {
		System.out.print(s);
		return scanner.nextLine();
	}

	public static void cls() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
		}
	}

	public static String[] splitFirst(String input, String target) {
		int firstDotIndex = input.indexOf(target);
		if (firstDotIndex == -1) {
			return new String[] { input, "" };
		}
		return new String[] { input.substring(0, firstDotIndex), input.substring(firstDotIndex + 1) };
	}

	public static String[] splitLast(String input, String target) {
		int lastDotIndex = input.lastIndexOf(target);
		if (lastDotIndex == -1) {
			return new String[] { input, "" };
		}
		return new String[] { input.substring(0, lastDotIndex), input.substring(lastDotIndex + 1) };
	}

	public static List<String> dumpCmdline(String cmdline) {
		String currentArg = "";
		List<String> argv = new ArrayList<>();
		int getStringSign = 0;
		cmdline = cmdline.replace('\'', '"');
		for (int i = 0; i < cmdline.length(); i++) {
			String buf = String.valueOf(cmdline.charAt(i));
			if (buf.equals("\"") || buf.equals("\'")) {
				getStringSign++;
			}
			currentArg += buf.equals("\"") || (buf.contains(" ") && getStringSign % 2 == 0) ? "" : buf;
			if (buf.equals(" ") && (getStringSign % 2 == 0)) {
				if (!currentArg.equals(" ")) {
					argv.add(currentArg);
				}
				currentArg = "";
			}
		}
		argv.add(currentArg);
		return argv;
	}

	public static void selectCopyFile(String sourceDir, String targetDirForSpecified, String targetDirForOthers, List<String> specifiedSuffixes) throws IOException {
		Files.createDirectories(Paths.get(targetDirForSpecified));
		Files.createDirectories(Paths.get(targetDirForOthers));
		Files.walk(Paths.get(sourceDir)).filter(Files::isRegularFile).forEach(sourcePath -> {
			try {
				String fileName = sourcePath.getFileName().toString();
				int dotIndex = fileName.lastIndexOf('.');
				String suffix = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
				String targetDir = specifiedSuffixes.contains(suffix.toLowerCase()) ? targetDirForSpecified : targetDirForOthers;
				Path relativePath = Paths.get(sourceDir).relativize(sourcePath);
				Path targetPath = Paths.get(targetDir, relativePath.toString());
				Files.createDirectories(targetPath.getParent());
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
