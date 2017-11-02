/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campis.dp1.models;

import java.util.ArrayList;

/**
 *
 * @author sergio
 */
public class CNode {
    Coord ubication = new Coord();
    ArrayList<Coord> paths=new ArrayList<>();
    ArrayList<Integer> costs=new ArrayList<>();
    
    public CNode(){
      super();
    }
    
    public ArrayList<Coord> getPaths(){
        return this.paths;
    }

    public Coord getUbication() {
        return ubication;
    }

    public ArrayList<Integer> getCosts() {
        return costs;
    }
    
    
    
    public CNode(Coord coord){
        this.ubication = new Coord(coord.y,coord.x);
    }
    
    public void addPath(Coord coord){
        for (Coord path : paths) {
            if (path.x == coord.x && path.y ==coord.y) return;
        }
        this.paths.add(coord);
        Integer cost = new Integer(calculateCost(ubication,coord));
        this.costs.add(cost);
    }
    
    public void delPath(Coord coord){
        for (Coord path : paths) {
            if ((path.y == coord.y) && (path.x == coord.x)){
                int index = this.paths.indexOf(path);
                this.paths.remove(index);
                this.costs.remove(index);
            }
        }
    }
    
    public Coord getPos(){
        return new Coord(this.ubication.y,this.ubication.x);
    }

    private int calculateCost(Coord ubication, Coord coord) {
        // Obstacle calculation is done before, so we use pitagoric distance between the points
        return (int) ((ubication.x-coord.x)^2+(ubication.y-coord.y)^2)^(1/2);
    }
}
