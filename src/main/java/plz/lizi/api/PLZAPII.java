package plz.lizi.api;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import sun.misc.Unsafe;

public class PLZAPII {
	private static boolean isInitial = false;

	public static void init(String dir) {
		if (!isInitial) {
			isInitial = true;
			try {
				Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor();
				c.setAccessible(true);
				Unsafe unsafe = c.newInstance();
				Field f = Class.forName("jdk.internal.loader.NativeLibraries").getDeclaredField("loadedLibraryNames");
				Set<String> loadedDlls = ((Set<String>) unsafe.getObject(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f)));
				new ArrayList<>(loadedDlls).forEach(dllName -> {
					if (dllName.contains("plzapi"))
						loadedDlls.remove(dllName);
				});
			} catch (Throwable var3) {
			}
			try {
				Files.copy(Objects.requireNonNull(PLZAPII.class.getResourceAsStream("/plz/lizi/api/plzapi-x64.dll")), Path.of(dir + "\\plzapi-x64.dll"), StandardCopyOption.REPLACE_EXISTING);
				Files.copy(Objects.requireNonNull(PLZAPII.class.getResourceAsStream("/plz/lizi/api/plzapi-x86.dll")), Path.of(dir + "\\plzapi-x86.dll"), StandardCopyOption.REPLACE_EXISTING);
			} catch (Throwable ex) {
				System.out.println("[PLZAPI] Failed to release dlls !");
			}
			try {
				System.load(dll(dir));
			} catch (Throwable ex) {
				System.out.println("[PLZAPI] Failed to load dll.");
				throw new RuntimeException(ex);
			}
		}
	}
	
	static String dll(String dir) {
		String arch = System.getProperty("os.arch");
		if (arch.contains("64")) {
			return dir + "\\plzapi-x64.dll";
		} else if (arch.contains("86") || arch.contains("32")) {
			return dir + "\\plzapi-x86.dll";
		} else {
			throw new SystemNotSupportError("[EOP] System not support -> OS:" + System.getProperty("os.name") + ", JVM:" + System.getProperty("sun.arch.data.model") + ", OS_ARCH:" + System.getProperty("os.arch"));
		}
	}

	public static native Object getFieldPro(Object object, String name, String sign);

	public static native Object getStaticFieldPro(Class<?> clazz, String name, String sign);

	public static native void setFieldPro(Object object, String name, String sign, Object newObject);

	public static native void setStaticFieldPro(Class<?> clazz, String name, String sign, Object newObject);

	public static native void killThread(Thread thread);

	public static native void cKlassPtr(Object obj, Class<?> klass);

	public static native void loadJava(String javabin);

	public static native void attachDefineClass(String transform);

	public static native Instrumentation createInstrumentation();

	public static native void initLastRender(Class<?> caller, Method render);

	public static native void attachMethodToThread(Class<?> caller, Method method, String name, boolean hide);

	public static native void attachRunableToThread(Runnable runnable, String name, boolean hide);

	public static native Object[] getClassInstances(Class<?> klass);

	public static native void addClassPrepareCallback(String transform);
}
