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

    /* Serialize the blob to a file*/
    public void toFile(File targetDir) {
        String fileName = sha1(this.contents);
        File outFile = Utils.join(targetDir, fileName);
        writeObject(outFile, this);
    }

    /* load a file*/
    //todo

}
