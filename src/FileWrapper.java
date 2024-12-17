import java.io.Serializable;

public class FileWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recipient;
    private String sender;
    private String filename;
    private byte[] content;

    public FileWrapper(String recipient, String sender, String filename, byte[] content) {
        this.recipient = recipient;
        this.filename = filename;
        this.content = content;
        this.sender = sender;
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

    public String getSender() {
        return sender;
    }
}
