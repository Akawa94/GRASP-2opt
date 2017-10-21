

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args){
        WMap wmap = new WMap();
        // testing
        wmap.show();

        Products products = new Products();
        // testing
        products.show();


        Grasp grasp = new Grasp(wmap,0.5,products);
        ArrayList<graspElement> solution = grasp.Solution(products);

        // graspElement.print_list(solution);
        System.out.println("La solucion final fue: ");
        grasp.print_solution(solution);

    }
}
