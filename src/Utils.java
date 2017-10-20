import java.util.ArrayList;

public class Utils {
    public int total_cost(ArrayList<graspElement> list){
        int total_cost = 0;
        for (graspElement elem: list){
            if (elem.cost!=-1){
                total_cost+=elem.cost;
            }
        }
        return total_cost;
    }
}
