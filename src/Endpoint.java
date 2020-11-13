import java.util.Hashtable;

public class Endpoint {
    public String name, url;
    public int id;
    public Hashtable<String,Double> endpointVectors;

    public Endpoint(int id, String name, String url, Hashtable<String, Double> endpointVectors) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.endpointVectors = endpointVectors;
    }
}