import java.io.Serializable;

public class FileWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recipient;
    private String filename;
    private byte[] content;

    public FileWrapper(String recipient, String filename, byte[] content) {
        this.recipient = recipient;
        this.filename = filename;
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }
}
