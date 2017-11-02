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
class Route {
    private ArrayList<Coord> paths = new ArrayList<>();
    private int cost = 0;
    
    
    public void increaseCost(int sum){
        this.cost+=sum;
    }
            
    public Route() {
        super();
    }

    public Route(Route copy) {
        this.paths = new ArrayList<>(copy.getPaths());
        this.cost = copy.getCost();
    }

    public ArrayList<Coord> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Coord> paths) {
        this.paths = paths;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public void addPath(Coord coord){
        this.paths.add(coord);
    }
    
    
    
}
