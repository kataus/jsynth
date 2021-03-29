package pro.esteps.jsynth;

import pro.esteps.jsynth.console.TestConsole;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        TestConsole console = new TestConsole();
        console.processConsoleInput();
    }
}
