package utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Folder {
    private final File file;

    private Folder(File file) {
        this.file = file;
    }

    public String getPath(){
        return this.file.getPath();
    }

    public List<Folder> getSubFolders() {
        List<Folder> subFolders = new LinkedList<Folder>();
        for (File entry : file.listFiles()) {
            if (entry.isDirectory()) {
                try {
                    subFolders.add(Folder.fromDirectory(entry));
                } catch (IOException e) {
                    System.out.println("FOLDER");
                    e.printStackTrace();
                }
            }
        }
        return subFolders;
    }

    public List<Document> getDocuments() {
        List<Document> documents = new LinkedList<Document>();
        for (File entry : file.listFiles()) {
            if (!entry.isDirectory() && entry.getName().endsWith("java")){
                try {
                    documents.add(Document.fromFile(entry));
                } catch (IOException e) {
                    System.out.println("Document");
                    e.printStackTrace();
                }
            }
        }
        return documents;
    }

    public static Folder fromDirectory(File dir) throws IOException {
        return new Folder(dir);
    }
}