package gitlet;
import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class blob implements Serializable{
    private String fileName;
    private byte[] contents;

    public blob(File file) {
        this.fileName = file.getName();
        this.contents = readContents(file);
    }

    public String getSHA1() {
        return sha1(this.contents);
    }

    public String getFileName() { return this.fileName; }

    /* Serialize the blob to a file*/
    public void toFile(File filePath) {
        writeObject(filePath, this);
    }

    /* load a file*/
    //todo

}
