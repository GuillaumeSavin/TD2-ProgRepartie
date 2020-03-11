package progRepartie;

public class Neighbor
{
    public String username, ip;

    public Neighbor(String ipv4, String name)
    {
        this.ip = ipv4;
        this.username = name;
    }

    @Override
    public int hashCode()
    {
        return ip.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass() == Neighbor.class && o.hashCode() == hashCode();
    }
}
