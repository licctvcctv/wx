package com.wechat.studygame.util;

public class ScriptExecutor {

    public static void executePowerShellScript() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", "src/main/resources/sql/test.ps1");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
