package progRepartie;

import java.io.Serializable;
import java.util.Random;

public class Message_backup implements Serializable
{
    public final long id = new Random().nextLong();
    private static final long serialVersionUID = 1L;
    public Object content;

    public Message_backup(Object content)
    {
        this.content = content;
    }

    public Object getContent()
    {
        return content;
    }

}
