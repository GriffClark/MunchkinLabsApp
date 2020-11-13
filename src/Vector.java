import java.util.Hashtable;
import java.util.Set;

public class Vector {
    public Hashtable<String, Double> categoryScoreTable = new Hashtable<>();  // category:score

    public Vector() {

    }

    public Vector(Hashtable<String, Double> table){
        this.categoryScoreTable = table;
    }

    public double getDistanceFrom(Vector otherVector){
        Set<String> keys = categoryScoreTable.keySet();
        double sum = 0;
        for(String key: keys) {
            if(!key.equals("FINANCE")){ // I screwed up when storing this value. It will skew the whole thing TODO: fix
                sum += Math.abs(categoryScoreTable.get(key) - otherVector.categoryScoreTable.get(key));
            }
        }
        return sum;
    }
}
