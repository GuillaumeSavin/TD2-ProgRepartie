package progRepartie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public final long id = new Random().nextLong();
    public Object content;
    public final List<String> senders = new ArrayList<>();

    public Message(Object content)
    {
        this.content = content;
    }

    public Object getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        return senders + "> " + content;
    }
}
