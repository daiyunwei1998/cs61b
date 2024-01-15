package gitlet;
import java.io.File;
import java.io.Serializable;
import java.sql.Blob;

import static gitlet.Utils.*;

public class blob implements Serializable{
    private String fileName;
    private String contents;

    public blob(File file) {
        this.fileName = file.getName();
        this.contents = readContentsAsString(file);
    }

    public String getSHA1() {
        return sha1(this.contents);
    }

    public String getFileName() { return this.fileName; }
    public String getContent() {
        return this.contents;
    }

    /* Serialize the blob to a file*/
    public void toFile(File filePath) {
        writeObject(filePath, this);
    }
    public void toOriginalFile(File filePath) {
        writeContents(filePath, this.contents);
    }

    /* load a file */
    public static blob readBlob(File filePath) {
        blob b = readObject(filePath, blob.class);
        return b;
    }

}
