import java.util.Hashtable;

public class Endpoint {
    public String name, url;
    public int id;
    public Vector vector;

    public Endpoint(int id, String name, String url, Vector vector) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.vector = vector;
    }
}