/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campis.dp1.models.routing;

import campis.dp1.models.CGraph;
import campis.dp1.models.CNode;
import campis.dp1.models.Coord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author sergio
 */
public class Grasp {
    // private RouteDictionary distances= new RouteDictionary(); Optimization will be implemented later
    private ArrayList<Coord> prodList;
    private CGraph paths;
    private int[][] map;
    private double alpha = 0.3;
    private int n_grasp_routes=100;
    
    public Grasp(int[][] real_map, CGraph routesGraph, ArrayList<Coord> batchesCoords){
        this.map=real_map;
        this.paths = routesGraph;
        this.prodList = batchesCoords;
    }
    
    public GraspResults execute() {
        ArrayList<Coord> returnable = new ArrayList<>();
        ArrayList<Coord> returnable_p = new ArrayList<>();
        ArrayList<Coord> graspList = new ArrayList<>(this.prodList);
        // we start from coord 0,0
        returnable.add(new Coord(0,0));
        while(graspList.size()>0){
            ArrayList<Route>  routes = new ArrayList<>();
            for (int i = 0; i < graspList.size(); i++) {
                // always the last of the returnable route
                Coord c_destiny = normalize_start(graspList.get(i));
                routes.add(calculateRoute(returnable.get(returnable.size()-1),c_destiny));
            }
            
            // Sort the paths
            sortRoutes(routes,graspList);
            Random generator = new Random();
            int k = generator.nextInt((int)(routes.size()*alpha+1));
            Route route_chosen=routes.remove(k); // Selected route
            returnable_p.add(graspList.remove(k)); // Remove from graspList
            for (int i = 0; i < route_chosen.getPaths().size(); i++) {
                returnable.add(route_chosen.getPaths().get(i));
            }
            
            
            if (graspList.size()==0){
                // special case before going out
                Route final_route = calculateRoute(returnable.get(returnable.size()-1),new Coord(0,0));
                ArrayList<Coord> final_route_coords=final_route.getPaths();
                for (int i = 0; i < final_route_coords.size(); i++) {
                    returnable.add(final_route_coords.get(i));
                }
                break;
            }
        }
        
        return new GraspResults(returnable,returnable_p);
    }
    
    
    private Coord normalize_start(Coord c_prod){
        Coord retornable = new Coord(c_prod.y,c_prod.x);
        if (c_prod.y==0 && c_prod.x==0) return retornable;
        else{
            // for loop to search for front
            for (int i = -1; i <2; i++) {
                for (int j = -1; j <2; j++) {
                 try{
                     if ((i!=j) && this.map[c_prod.y+i][c_prod.x+j]==0){
                         retornable = new Coord(c_prod.y+i,c_prod.x+j);
                         return retornable;
                     }
                 }catch(Exception e){

                 }
                }
            }
        }
        return retornable;
    }

    private void sortRoutes(ArrayList<Route> routes, ArrayList<Coord> graspList) {
        for(int i=0; i<routes.size();i++){
            for (int j = 0; j < routes.size(); j++) {
                if (routes.get(i).getCost() < routes.get(j).getCost() && j<1){
                    // swap routes
                    Route swap = new Route(routes.get(i));
                    routes.set(i, routes.get(j));
                    routes.set(j, swap);
                    // swap graspList
                    Coord swap_c = new Coord(graspList.get(i));
                    graspList.set(i, graspList.get(j));
                    graspList.set(j, swap_c);
                }
            }
        }
    }

    private Route calculateRoute(Coord c_origin, Coord c_destiny) {
        Route returnable = new Route();
        // Will calculate route backwards
        ArrayList<CNode> destiny_nodes= new ArrayList<>();
        ArrayList<CNode> origin_nodes= new ArrayList<>();
        System.out.println("Origin coordinate is: [ "+c_origin.y+", "+c_origin.x+"] n Destiny coordinate is: [ "+c_destiny.y+", "+c_destiny.x+"].");
        ArrayList<String> keys =  new ArrayList<String>(this.paths.keySet());
        for (int i = 0; i < keys.size(); i++) {
            CNode node = this.paths.getNode(keys.get(i));
            if (adyacent_c(node.getPos(),c_destiny)==true){
                destiny_nodes.add(node);
                if (destiny_nodes.size()==2) break;
            }            
        }
        
        for (int i = 0; i < keys.size(); i++) {
            CNode node = this.paths.getNode(keys.get(i));
            if (adyacent_c(node.getPos(),c_origin)==true){
                origin_nodes.add(node);
                if (origin_nodes.size()==2) break;
            }            
        }
        
        Random generator = new Random();
        CNode destiny_corner = destiny_nodes.remove(generator.nextInt(destiny_nodes.size()));
        CNode origin_corner = origin_nodes.remove(generator.nextInt(origin_nodes.size()));
        // will use pitagoric distance, only moving between nodes
        returnable.addPath(c_destiny);
        System.out.println("Entering calculateRoute while loop");
        Coord previous_c = null;
        Boolean exit_loop;
        while (same_coord(destiny_corner.getPos(),origin_corner.getPos())==false){
            exit_loop=false;
            System.out.print("\nCoords are: ");
            origin_corner.getPos().print_c();
            System.out.print(" , ");
            destiny_corner.getPos().print_c();
            System.out.println(" .");
            ArrayList<Integer> aux_costs = new ArrayList<>();
            ArrayList<Coord> aux_nodes = new ArrayList<>();
            for (int i = 0; i < destiny_corner.getPaths().size(); i++) {
                Coord ver_coord = destiny_corner.getPaths().get(i);
                if (previous_c!=null &&  same_coord(ver_coord,previous_c)) continue;
                if (same_coord(ver_coord,origin_corner.getPos())){
                     Coord chosen = ver_coord;
                    returnable.increaseCost(chosen.c_distance(returnable.getPaths().get(returnable.getPaths().size()-1)));
                    returnable.addPath(new Coord(chosen));
                    destiny_corner = this.paths.getNode(chosen.y, chosen.x);
                    exit_loop = true;
                    break;
                }
                if (previous_c!=null && ver_coord.c_distance(origin_corner.getPos()) >= previous_c.c_distance(origin_corner.getPos()))
                    continue;
                aux_nodes.add(ver_coord);
                int cost = ver_coord.c_distance(c_origin);
                aux_costs.add(cost);
                
            }
            if (exit_loop==true) break;
            if (aux_nodes.size()==0){
                // we go backwards one step
                returnable.getPaths().remove(returnable.getPaths().size()-1);
                destiny_corner = this.paths.getNode(previous_c.y,previous_c.x);
                if (returnable.getPaths().size()==0) previous_c=null;
                else previous_c = new Coord(returnable.getPaths().get(returnable.getPaths().size()-1));
            }else{
                // sorting per cost
                sortPerCost(aux_costs,aux_nodes);
                Coord chosen = aux_nodes.get(generator.nextInt((int) (aux_nodes.size()*alpha+1)));
                returnable.increaseCost(chosen.c_distance(returnable.getPaths().get(returnable.getPaths().size()-1)));
                returnable.addPath(new Coord(chosen));
                previous_c = new Coord(destiny_corner.getPos());
                destiny_corner = this.paths.getNode(chosen.y, chosen.x);
            }
            
        }
        if (same_coord(c_origin, returnable.getPaths().get(returnable.getPaths().size()-1)) == true){
            reverseRoute(returnable);
            return returnable;
        }
        returnable.increaseCost(c_origin.c_distance(returnable.getPaths().get(returnable.getPaths().size()-1)));
        returnable.addPath(c_origin);
        reverseRoute(returnable);
        return returnable;
    }

    private boolean adyacent_c(Coord pos, Coord c_destiny) {
        int rack_n=0;
        int c_i;
        int c_f;
        int opt;
        if (pos.x != c_destiny.x && pos.y != c_destiny.y) return false;
        if (pos.x==c_destiny.x) opt = 1;
        else opt=0;
        if (opt==0){
            c_i=pos.x;
            c_f=c_destiny.x;
        }else{
            c_i=pos.y;
            c_f=c_destiny.y;
        }
        int init;
        int fin;
        if (c_i<c_f){
            init=c_i;
            fin=c_f;
        }else{
            init=c_f;
            fin=c_i;
        }
        if (opt==0){
            for (int k = init; k<fin ;k++){
                if (map[pos.y][k]==1){
                    return false;
                }
            }
        }else{
            for (int k = init; k<fin ;k++){
                if (map[k][pos.x]==1){
                    return false;
                }
            }
        }

        return true;
    }

    private void sortPerCost(ArrayList<Integer> aux_costs, ArrayList<Coord> aux_nodes) {
        
        for (int i = 0; i < aux_costs.size(); i++) {
            for (int j = 0; j < aux_costs.size(); j++) {
                if (aux_costs.get(i) < aux_costs.get(j) && j<i){
                    // For costs
                    Integer swap = new Integer(aux_costs.get(i));
                    aux_costs.set(i, aux_costs.get(j));
                    aux_costs.set(j, swap);
                    
                    // For coords
                    Coord swap_c = new Coord(aux_nodes.get(i));
                    aux_nodes.set(i, aux_nodes.get(j));
                    aux_nodes.set(j, swap_c);
                }
            }
        }
    }

    private boolean same_coord(Coord c1, Coord c2) {
        return (c1.y == c2.y && c1.x == c2.x);
    }

    private void reverseRoute(Route returnable) {
        for (int i = 0, j = returnable.getPaths().size() -1;i<j; i++) {
            returnable.getPaths().add(i,returnable.getPaths().remove(j));
            
     }
    }
    
}
