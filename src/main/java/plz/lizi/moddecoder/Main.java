package plz.lizi.moddecoder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import plz.lizi.api.MCDeobfHelper;
import plz.lizi.api.PLZBASE;
import static plz.lizi.api.PLZBASE.*;

public class Main {

	public static void main(String[] args) {
		while (true) {
			cls();
			println("* Mod Decoder by PLZLiZi *");
			println();
			println("- Working path : " + new File(".\\").getAbsolutePath());
			println("- Command : dec (path to mod.jar) (path to extract folder) [-W] [-D]");
			println("- Command : ref (.tsrg file path or /MCVERSION)");
			println("- Command : get (srg / mcp / sign -> line)");
			println("- [] is optional content");
			println("- [-W] auto generate src workspace");
			println("- [-D] auto anti obfuscate for MINECRAFT SRG");
			println();
			String cmd = input("> ");
			long millis = System.currentTimeMillis();
			if (cmd.equals("exit") || cmd.equals("quit") || cmd.isEmpty())
				System.exit(0);
			List<String> values = dumpCmdline(cmd);
			if (values.get(0).equals("dec")) {
				boolean workspace = cmd.contains("-W");
				boolean deobf = cmd.contains("-D");
				if (deobf) {
					try {
						MCDeobfHelper.init("/1.20.1");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (values.size() >= 3) {
					String from = values.get(1);
					String to = values.get(2);
					File jar = new File(from);
					try {
						ConsoleDecompiler.main(new String[] { from, to });
						if (jar.isFile()) {
							if (deobf) {
								MCDeobfHelper.extractJarWithDeobf(to + "\\" + jar.getName(), to + "\\" + splitLast(jar.getName(), ".")[0]);
							} else {
								extractJar(to + "\\" + jar.getName(), to + "\\" + splitLast(jar.getName(), ".")[0]);
							}
							new File(to + "\\" + jar.getName()).delete();
							if (workspace) {
								PLZBASE.selectCopyFile(to + "\\" + splitLast(jar.getName(), ".")[0], to + "\\" + splitLast(jar.getName(), ".")[0] + "-src\\main\\java", to + "\\" + splitLast(jar.getName(), ".")[0] + "-src\\main\\resources", Arrays.asList("java"));
							}
						}
						println("- DECOMPILE SUCCESFUL in " + String.format("%.1f", (double) (System.currentTimeMillis() - millis) / 1000) + "s");
					} catch (Exception e) {
						new File(to + "\\" + jar.getName()).delete();
						println("- DECOMPILE FAILED in " + String.format("%.1f", (double) (System.currentTimeMillis() - millis) / 1000) + "s with a exception:");
						e.printStackTrace();
					}
				} else {
					println("- Too few parameters");
					println("- DECOMPILE FAILED in " + String.format("%.1f", (double) (System.currentTimeMillis() - millis) / 1000) + "s");
				}
			} else if (values.get(0).trim().equals("ref")) {
				if (values.size() >= 2 && !values.get(1).isEmpty()) {
					try {
						MCDeobfHelper.init(values.get(1));
						println("- SET REFMAP SUCCESSFUL to " + values.get(1));
					} catch (Exception e) {
						println("- DECOMPILE FAILED with an exception :");
						e.printStackTrace();
					}
				} else {
					println("- Refmap : " + MCDeobfHelper.REFMAP);
				}

			}else if (values.get(0).trim().equals("get") && values.size()>=2 && !values.get(1).isEmpty()) {
				
			}
			println();
			input("> Press any key backing to continue...");
		}
	}
}
