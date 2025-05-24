package plz.lizi.api;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface PLZAPIA extends Library {
	static String dll() {
		String arch = System.getProperty("os.arch");
		if (arch.contains("64")) {
			return "/plz/lizi/api/plzapi-x64.dll";
		} else if (arch.contains("86") || arch.contains("32")) {
			return "/plz/lizi/api/plzapi-x86.dll";
		} else {
			throw new SystemNotSupportError("[EOP] System not support -> OS:" + System.getProperty("os.name") + ", JVM:" + System.getProperty("sun.arch.data.model") + ", OS_ARCH:" + System.getProperty("os.arch"));
		}
	}

	PLZAPIA INSTANCE = Native.load(dll(), PLZAPIA.class);

	void overScreen(String wc, String wp);

	void overwriteWndproc(String wc);

	boolean isKeyDown(int key);

	int cursorPos(int tag);

	String currentWork();

	void ret(String dllp, String fn);

	void unret(String dllp, String fn);

	void allret(String dllp);

	void startWindowWatcher(String wc);

	void stopWindowWatcherAndRender();

	void startTimestopRender(String wc);

	void stopTimestopRender(String wc);

	void setProcessSpeed(double speed);

	void attachCurrentProcess();

	String getCmdLine();

	void lockWindowTextTo(String text);

	void killNative();

	String getCurrentJVM();
}
