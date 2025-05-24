package plz.lizi.api;

import static plz.lizi.api.PLZBASE.readFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import plz.lizi.moddecoder.Main;

public class MCDeobfHelper {
    public static String REFMAP = "/1.20.1";
    public static boolean firstUse = true;
    public static Map<String, String> MCP2SRG = new HashMap<>();
    public static Map<String, String> SRG2MCP = new HashMap<>();
    public static String FSPLITER = "f_\\d+_", MSPLITER = "m_\\d+_";

    public static Map<String, String> makeReflect(String input) {
        Map<String, String> nameMap = new HashMap<>();
        String[] lines = input.split("\\R");
        for (String line : lines) {
            Matcher memberMatcher = Pattern.compile("^(.*?)\\s+" + FSPLITER + "$").matcher(line);
            if (memberMatcher.find()) {
                nameMap.put(memberMatcher.group(0).split(" ")[1].trim(), memberMatcher.group(1).trim());
                continue;
            }
            Matcher methodMatcher = Pattern.compile("^(.*?)\\s+[^\\s]+\\s+" + MSPLITER + "$").matcher(line);
            if (methodMatcher.find()) {
                nameMap.put(methodMatcher.group(0).split(" ")[2].trim(), methodMatcher.group(1).trim());
            }
        }

        return nameMap;
    }

    public static void init(String cvt) throws Exception {
        if (REFMAP != cvt || firstUse) {
            firstUse = false;
            REFMAP = cvt;
            String cvtFile = REFMAP.startsWith("/") ? new String(PLZBASE.readAllBytes(Main.class.getResourceAsStream("/tsrg" + REFMAP + ".tsrg"))) : readFile(REFMAP);
            SRG2MCP = makeReflect(cvtFile);
            SRG2MCP.forEach((srg, mcp) -> {
                MCP2SRG.put(mcp, srg);
            });
        }
        System.out.println("- Find symbols : srg2mcp->" + SRG2MCP.size() + " / mcp2srg->" + MCP2SRG.size());
    }

    public static List<String> splitCodeWithSRG(String input) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(FSPLITER+"|"+MSPLITER);
        Matcher matcher = pattern.matcher(input);
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.add(input.substring(lastEnd, matcher.start()));
            }
            result.add(matcher.group());
            lastEnd = matcher.end();
        }
        if (lastEnd < input.length()) {
            result.add(input.substring(lastEnd));
        }
        return result;
    }

    public static String deobfCode(String obf) {
        String deobf = "";
        List<String> obfs = splitCodeWithSRG(obf);
        for (String obfPart : obfs) {
            String tryDeobf = SRG2MCP.get(obfPart);
            if (tryDeobf == null) {
                deobf += obfPart;
            } else {
                deobf += tryDeobf;
            }
        }
        return deobf;
    }

    public static void extractJarWithDeobf(String jarPath, String destDir) throws IOException {
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
                    byte[] buffer = PLZBASE.readAllBytes(jarInputStream);
                    if (entry.getName().endsWith(".java")) {
                        String code = new String(buffer);
                        outputStream.write(deobfCode(code).getBytes());
                    } else {
                        outputStream.write(buffer);
                    }

                }
            }
        }
    }
}
