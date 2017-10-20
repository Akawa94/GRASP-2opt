

import java.util.ArrayList;


public class Main {

    public static void main(String[] args){
        WMap wmap = new WMap();
        // testing
        wmap.show();

        Products products = new Products();
        // testing
        products.show();

        Grasp grasp = new Grasp(wmap,0.5);
        grasp.Initialize(products);
        ArrayList<graspElement> solution = grasp.Solution(products);

        // graspElement.print_list(solution);
        grasp.print_solution(solution);

    }
}
