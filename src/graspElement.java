import java.util.ArrayList;
import java.util.Comparator;

public class graspElement{
    Coord coord;
    int distances_index;
    int cost;

    public graspElement(){

    }
    public graspElement(Coord c, int i,int k){
        super();
        this.coord = c;
        this.distances_index = k;
        this.cost = i;
    }

    public static void print_list(ArrayList<graspElement> list){
        int total_cost=0;
        for (graspElement elem:list){
            System.out.println("Producto de coords\t [ "+elem.coord.y+", "+elem.coord.x + "] and cost\t "+elem.cost);
            total_cost+=elem.cost;
        }
        System.out.println("El costo total de la ruta fue: "+total_cost);
    }

    Comparator<graspElement> cmp = Comparator
            .comparing((graspElement ge)-> ge.cost);


}
