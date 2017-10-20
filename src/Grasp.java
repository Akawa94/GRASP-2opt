import java.util.*;

public class Grasp {
    private Route[][] distances; // grid of distances for future check
    private WMap wmap;
    private double alpha = 1;
    private int n_grasp_routes=8;
    // private Products prods;

    private Coord origin_prod(Coord c_prod){
        int[] prod = new int[2];
        prod[0]=c_prod.y;
        prod[1]=c_prod.x;

        int[] origin_prod=new int[2];

        // for bt
        if (prod[0]==0 && prod[1]==0){
            origin_prod = prod.clone();
            return new Coord(origin_prod[0],origin_prod[1]);
        }

        // Free point in front of product

        if (!(prod[0]==0 && prod[1]==0)){
            origin_prod[0]=prod[0];
            origin_prod[1]=prod[1];

            // Should expand logic, depending on rack orientation and such
            if (prod[0]>0 && this.wmap.getVal(prod[0]-1,prod[1]) == 0){
                origin_prod[0]=prod[0]-1;
            }else if (prod[1]>0 && this.wmap.getVal(prod[0],prod[1]-1) == 0){
                origin_prod[1]=prod[1]-1;
            }else if (prod[0]-1<this.wmap.getSize()[0] && this.wmap.getVal(prod[0]+1,prod[1]) == 0){
                origin_prod[0]=prod[0]+1;
            }else{
                origin_prod[1]=prod[1]+1;
            }
        }else{
            origin_prod[0]=0;
            origin_prod[1]=0;
        }
        return new Coord(origin_prod[0],origin_prod[1]);
    }

    private int[][] markdown_map(int[][] map,Coord c1,Route route){
        Coord ant=c1;
        int[][] retornable = map;
        for (Coord c:route.esquinas){
            for (int i = 0; i < Math.abs(ant.x-c.x + (ant.y-c.y)); i++) {
                if (ant.y==c.y){
                    map[c.y][ant.x+i*(-1)*(ant.x-c.x)/(Math.abs(ant.y-c.y))]=2;
                }else{
                    map[c.y+i*(-1)*(ant.y-c.y)/(Math.abs(ant.y-c.y))][ant.x]=2;
                }
                ant=c;
            }
        }
        return map;
    }



    private int distance(Route route,graspElement prod1, int dist_index_p1, graspElement prod2, int[][] map, int max_turns){
        // all in [y,x] format

        // Will advance in straight line as preferred, turns will only occur when not possible to advance

        // Directionality 'u' Up | 'd' Down | 'l' left | 'r' right
        int esc = this.distances[dist_index_p1][prod2.distances_index].cost;
        esc= (esc!=-1?esc:this.distances[prod2.distances_index][dist_index_p1].cost); // just in case
        if (esc != -1) return esc; // already saved in distances matrix

        // GRASP ROUTE LOGIC

        // Will generate n random routes of 3 turns each, then selecting one random in aplha range
        ArrayList<Route> route_bag = new ArrayList<Route>();
        int[][] costos = new int[this.n_grasp_routes][2];
        for (int i = 0; i < this.n_grasp_routes; i++) {
            route_bag.add(new Route(route,prod1.coord,prod2.coord,map));
            int size = route_bag.get(route_bag.size()-1).esquinas.size();
            // cogemos ultima coord
            Coord c_test = route_bag.get(route_bag.size()-1).esquinas.get(size-1);
            costos[i][0]=i;
            // will evaluate each route by its n_rack
            costos[i][1]=this.wmap.n_racks(new graspElement(c_test,-1,-1),prod2);
        }
         // sorts the list
        new Route().qs_sort(costos,0,this.n_grasp_routes-1);
        Random generator = new Random();
        int k = generator.nextInt((int)(costos.length*alpha));
        Route picked_route1=route_bag.remove(k);
        Coord last_c1 = picked_route1.esquinas.get(picked_route1.esquinas.size()-1);
        // markdown map
        int[][] map_c1=markdown_map(map.clone(),prod1.coord,picked_route1);

        // recursive
        int r_distance1 = distance(picked_route1,new graspElement(last_c1,-1,-1),dist_index_p1,prod2,map_c1,max_turns-3>=0?max_turns-3:0);
        // checks if it has been completed
        if (r_distance1 != -1) return r_distance1;

        // markdown 2nd map
        k = generator.nextInt((int)((costos.length-1)*alpha));
        Route picked_route2=route_bag.remove(k);
        Coord last_c2=picked_route2.esquinas.get(picked_route2.esquinas.size()-1);
        int[][] map_c2=markdown_map(map.clone(),prod1.coord,picked_route2);
        // recursive
        int r_distance2 = distance(picked_route2,new graspElement(last_c2,-1,-1),dist_index_p1,prod2,map.clone(),max_turns-3>=0?max_turns-3:0);
        // checks if it has been completed
        if (r_distance2 != -1) return r_distance2;

        return -1;
    }

    public Grasp(WMap wmap, double alpha){
        this.wmap=wmap;
        this.alpha=alpha;
    }

    public void Initialize(Products products){

        // Initialize distances matrix at -1
        // +1 for 0,0 origin
        this.distances = new Route[products.size()+1][products.size()+1];
        for(int i=0;i<products.size();i++){
            for(int j=0;j<products.size();j++){
                this.distances[i][j]=new Route();
                this.distances[i][j].cost=-1;
            }
        }
    }

    public ArrayList<graspElement> Solution(Products products) {
        // testing lol
//        int[] c_i = {1,1,1,1};
//        int[] c_j = {2,2,2,2};
//
//        return new Products(c_i,c_j);
        // Initialize distances matrix

        // Actualmente consideramos el origen como punto de inicio
        int turn_counter = 0;
        ArrayList<graspElement> graspSolution = new ArrayList<graspElement>();
        ArrayList<graspElement> graspList = new ArrayList<graspElement>();

        graspSolution.add(new graspElement(new Coord(0,0),-1,-1));

        for (int k =0; k< products.size();k++){
            graspList.add(new graspElement(products.getProduct(k),-1,k+1));
        }

        // Core del Grasp

        while (turn_counter <= products.size()){
            // Primera comparación
            if (turn_counter==0 || turn_counter<products.size()){
                if (turn_counter==0){
                // Del origen al primer elemento
                    for( graspElement elem : graspList){
                        graspElement ant = new graspElement(new Coord(0,0),0,0);
                        elem.cost = distance(new Route(),ant,ant.distances_index,elem,this.wmap.cpReal_map(),this.wmap.n_racks(ant,elem));
                    }
                }else if(turn_counter<products.size()){
                    for(graspElement elem : graspList){
                        graspElement ant = graspSolution.get(turn_counter);
                        elem.cost = distance(new Route(),ant,ant.distances_index,elem,this.wmap.cpReal_map(),this.wmap.n_racks(ant,elem));
                    }
                }
                // we sort the list
                graspList.sort((Comparator<? super graspElement>) new graspElement().cmp);

                // elegimos dentro de un rango, la mejor solucion
                Random generator = new Random();
                System.out.println(turn_counter);
                System.out.println(graspList.size());
                int rand_int = generator.nextInt((int)(graspList.size()*alpha)+1);

                // Transfer of selected item
                graspSolution.add(graspList.remove(rand_int));

                // Reseting costs on graspList
                for (graspElement elem : graspList){
                    elem.cost=-1;
                }

            }else{
                    if(turn_counter == products.size()){
                        graspSolution.add(new graspElement(new Coord(0,0),-1,turn_counter+1));
                        graspElement ant = graspSolution.get(turn_counter);
                        graspElement act = new graspElement(new Coord(0,0),0,0);
                        graspSolution.get(turn_counter+1).cost = distance(new Route(),ant,ant.distances_index,act,this.wmap.cpReal_map(),this.wmap.n_racks(ant,act));
                        break;
                    }
                }
            turn_counter+=1;
        }


        return graspSolution;
    }

    public void print_solution(ArrayList<graspElement> solution) {
        int counter = 0;
        for (graspElement elem : solution){
            System.out.println("Mi producto n"+counter+" fue el:\t["+elem.coord.y+", "+elem.coord.x+"] y su costo fue: "+elem.cost);
            counter++;
        }
    }
}
