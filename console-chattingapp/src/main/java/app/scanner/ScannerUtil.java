package app.scanner;

import java.util.Scanner;

public class ScannerUtil {
    private static Scanner sc;
    public static Scanner getScanner() {
        if(sc != null) return sc;
        return sc = new Scanner(System.in);
    }
}
