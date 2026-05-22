import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileSystemTree {
    private static class TreeNode{
        final String name;
        Boolean isFile;
        final List<TreeNode> children = new ArrayList<>();
        TreeNode(String name, Boolean isFile){
            this.name = name;
            this.isFile = isFile;
        }

        void addChild(TreeNode child){
            children.add(child);
        }

        TreeNode findChild(String name){
            for (TreeNode child: children){
                if (child.name.equals(name)){
                    return child;
                }
            }
            return null;
        }
    }

    private final TreeNode root = new TreeNode("*", null);

    /**
     * Добавляет в дерево файловой системы все элементы, присутствующие
     * в полном пути к файлу или папке
     * @param path полный путь к файлу или папке вида
     *             C:\Folder1\SubFolder\SubFolder2\filename.ext
     */
    public void addPath(String path){
        Path filePath = Paths.get(path);
        Path rootPart = filePath.getRoot();
        String rootName = (rootPart != null) ? rootPart.toString() : "/";
        Boolean targetType = isFile(filePath);
        TreeNode current = root;
        TreeNode diskNode =current.findChild(rootName);
        if (diskNode == null){
            var rootType =  isFile(Paths.get(rootName));
            diskNode = new TreeNode(rootName, rootType);
            current.addChild(diskNode);
        }
        current = diskNode;

        for (int i = 0; i < filePath.getNameCount(); i++){
            Path pathPart = filePath.getName(i);
            String part = pathPart.toString();
            Path subPath = rootPart != null
                    ? rootPart.resolve(filePath.subpath(0, i + 1))
                    : filePath.subpath(0, i + 1);
            Boolean isFile = isFile(subPath);
            TreeNode child = current.findChild(part);
            if (child == null){
                child = new TreeNode(part, isFile);
                current.addChild(child);
            } else {
                if (child.isFile == null && isFile != null){
                    child.isFile = isFile;
                }
            }
            current = child;
        }
    }

    /**
     * Проверяет, чем является путь на диске.
     * @return true (файл), false (папка), null (не существует)
     */
    private Boolean isFile(Path path) {
        try {
            if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                return true;
            } else if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                return false;
            } else {
                // Существует, но не файл и не папка
                // Или не существует вовсе.
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void printTree(){
        System.out.println("===Файловая система===");
        printNode(root, "");
        System.out.println("======================");
    }

    private void printNode(TreeNode node, String indent){
        if (node == root){
            node.children.sort(Comparator.comparing(n->n.name));
            for (var child: node.children){
                printNode(child, "");
            }
            return;
        }
        String icon = "❌";
        if (node.isFile != null){
            icon = (node.isFile) ? "📄" : "📂";
        }
        System.out.println(indent + icon + " " + node.name);

        node.children.sort(Comparator.comparing(n->n.name));
        for (var child: node.children){
            printNode(child, indent + "\t");
        }
    }
}
