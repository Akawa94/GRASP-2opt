/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campis.dp1.models.routing;

import campis.dp1.models.Coord;
import java.util.ArrayList;

/**
 *
 * @author sergio
 */
public class GraspResults {
    ArrayList<Coord> route;
    ArrayList<Coord> products;
    GraspResults(ArrayList<Coord> returnable, ArrayList<Coord> returnable_p) {
        this.route = new ArrayList<>(returnable);
        this.products = new ArrayList<>(returnable_p);
    }

    public ArrayList<Coord> getRoute() {
        return route;
    }

    public ArrayList<Coord> getProducts() {
        return products;
    }
    
    
    
}
