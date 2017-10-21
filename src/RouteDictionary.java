import java.util.HashMap;
import java.util.Map;

public class RouteDictionary {
    private Map<String,Route> dict = new HashMap<>();

    public void insert(Coord p1, Coord p2, Route item){
        String key="[ "+p1.y+", "+p1.x+"] [ "+p2.y+", "+p2.x+"]";
        dict.put(key,item);
        return;
    }

    public Route retrieve(Coord p1, Coord p2){
        // Double retrieve
        String key1="[ "+p1.y+", "+p1.x+"] [ "+p2.y+", "+p2.x+"]";
        String key2="[ "+p2.y+", "+p2.x+"] [ "+p1.y+", "+p1.x+"]";
        if (dict.get(key1)!=null) return dict.get(key1);
        return(dict.get(key2));
    }
}
