void main() {
    FileSystemTree fileSystem = new FileSystemTree();

    String[] testPaths = {
            "C:\\Windows\\System32",
            "C:\\Windows\\notepad.exe",
            "C:\\Windows\\notepad2.exe",
            "C:\\Smak\\Programming\\Idea",
            "C:\\Smak\\Programming\\Idea\\FileSystemTree\\src\\FileSystemTree.java",
            "C:\\Smak\\Programming\\Idea\\FileSystemTree"
    };
    for (String p: testPaths){
        fileSystem.addPath(p);
        fileSystem.printTree();
        System.out.println();
    }
}