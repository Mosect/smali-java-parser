package com.mosect.smali.java.parser;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmaliTest {

    @Test
    public void testString() {
        String str = "\"getLifecycle() returned null in ComponentActivity\\'s constructor. Please make sure you are lazily constructing your Lifecycle in the first call to getLifecycle() rather than relying on field initialization.\"";
        SmaliParser smaliParser = new SmaliParser();
        SmaliParseResult<List<SmaliToken>> result = smaliParser.parseTokens(str);
        if (result.haveError()) {
            for (SmaliParseError error : result.getErrors()) {
                System.err.println(error);
            }
        }
    }

    @Test
    public void testNumber() {
        String str = ".field private static final C2:F = 3.49066E-4f";
        SmaliParser smaliParser = new SmaliParser();
        SmaliParseResult<List<SmaliToken>> result = smaliParser.parseTokens(str);
        if (result.haveError()) {
            for (SmaliParseError error : result.getErrors()) {
                System.err.println(error);
            }
        }
    }

    @Test
    public void testTokens() throws Exception {
        File dir = new File("E:\\Temp\\2022012118\\official-menglar-1.1.28-2022011810");
        List<File> files = new ArrayList<>();
        listSmaliFiles(dir, files);
        SmaliParser smaliParser = new SmaliParser();
        for (File file : files) {
            System.out.println("Smali: " + file);
            String text = readText(file);
            SmaliParseResult<List<SmaliToken>> result = smaliParser.parseTokens(text);
//            for (SmaliToken token : result.getResult()) {
//                if (token.getType() == SmaliTokenType.STRING) {
//                    System.out.println(token.getText());
//                }
//            }
            checkError(file, result);
            SmaliParseResult<SmaliTokenTreeNode> treeResult = smaliParser.parseTokenTree(result);
            checkError(file, treeResult);
        }
    }

    private void checkError(File file, SmaliParseResult<?> result) throws Exception {
        if (result.haveError()) {
            System.out.println("ErrorSmali: " + file);
            for (SmaliParseError error : result.getErrors()) {
                System.err.println(error);
            }
            throw new Exception("Invalid smali file: " + file);
        }
    }


    private static String readText(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream temp = new ByteArrayOutputStream(10240)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                temp.write(buffer, 0, len);
            }
            return temp.toString("UTF-8");
        }
    }

    private static void listSmaliFiles(File dir, List<File> out) {
        File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".smali")) {
                        out.add(file);
                    }
                } else if (file.isDirectory()) {
                    if ("..".equals(file.getName()) || ".".equals(file.getName())) continue;
                    listSmaliFiles(file, out);
                }
            }
        }
    }
}
