import java.util.*;

public class Grasp {
    // private Route[][] distances_old; // grid of distances_old for future check
    private RouteDictionary distances= new RouteDictionary();
    private WMap wmap;
    private double alpha = 0.4;
    private int n_grasp_routes=100;
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


    public Grasp(WMap wmap, double alpha,Products products){
        this.wmap=wmap;
        this.alpha=alpha;
        // Initialize distances_old matrix at -1, now dict instances
        // +1 for 0,0 origin
//        this.distances_old = new Route[products.size()+1][products.size()+1];
//        for(int i=0;i<=products.size();i++){
//            for(int j=0;j<=products.size();j++){
//                this.distances_old[i][j]=new Route();
//                this.distances_old[i][j].cost=-1;
//            }
//        }
    }

    private boolean already_found(graspElement p1, graspElement p2){
        if (distances.retrieve(p1.coord,p2.coord) != null){
            return true;
        }
        return false;
    }

    private Coord normalize_start(Coord c_prod){
        Coord retornable = new Coord(c_prod.y,c_prod.x);
        if (c_prod.y==0 && c_prod.x==0) return retornable;
        else{
            // for loop to search for front
            for (int i = -1; i <2; i++) {
                for (int j = -1; j <2; j++) {
                 try{
                     if ((i!=j) && this.wmap.getVal(c_prod.y+i,c_prod.x+j)==0){
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

    private int[][] sort_evaluations(int[][] arr, int min, int max){
        // brute force
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                if (arr[i][1] < arr[j][1] && j<i) {
                    int[] swap = arr[i].clone();
                    arr[i]=arr[j].clone();
                    arr[j]=swap.clone();
                }
            }
        }
        return arr;
    }




    private int distance(graspElement prod1, graspElement prod2, int[][] map, int max_turns){
        // all in [y,x] format
        // Will advance in straight line as preferred, turns will only occur when not possible to advance
        // route.print_map(map);

        // Normalize entry point, all in front of product prod1
        Coord c_normalized = normalize_start(prod1.coord);

        // Cloning first coord to iterate
        graspElement prod_iterator = new graspElement(new Coord(c_normalized.y,c_normalized.x),prod1.cost,prod1.distances_index);

        // Creating route iterator
        Route route_iterator = new Route();

        // Utils toolkit
        Utils utils = new Utils();

        for (int z = 0; z <max_turns; z++) {
            // If found in previous loop
            if (already_found(prod1,prod2)==true) return this.distances.retrieve(prod1.coord,prod2.coord).cost;

            // Else
            ArrayList<Route> route_bag = new ArrayList<Route>();
            int[][] evaluation_costs = new int[this.n_grasp_routes][2];
            for (int i = 0; i < this.n_grasp_routes; i++) {

                route_bag.add(new Route(route_iterator,prod_iterator.coord,prod2.coord,map));
                Route act_route =route_bag.get(route_bag.size()-1);
                ArrayList<Coord> ruta_coords= act_route.esquinas;
                int routes_coords_size = ruta_coords.size();
                // cogemos ultima coord
                Coord c_test = ruta_coords.get(routes_coords_size-1);
                evaluation_costs[i][0]=i;

                // will evaluate each route by certain criteria
                evaluation_costs[i][1]=utils.n_racks(c_test,prod2.coord,map) +
                                        c_test.c_distance(prod2.coord)*4 +
                                        act_route.cost*2 +
                                        routes_coords_size*10;
            }
            // sorts the list
            evaluation_costs = sort_evaluations(evaluation_costs,0,this.n_grasp_routes-1);
            // Should see if sorted

            Random generator = new Random();
            int k = generator.nextInt((int)(evaluation_costs.length*alpha));
            Route route_chosen=route_bag.remove(k);
            // Copy route
            route_iterator=new Route(route_chosen);

            // if complete solution
            if (route_iterator.esquinas.size()>0 && route_iterator.esquinas.get(route_iterator.esquinas.size()-1).front_of(prod2.coord)){

                // save in distances_old matrix, double insert already implemented in dict
                this.distances.insert(prod1.coord,prod2.coord, new Route(route_iterator));
                return route_iterator.cost;
            }
        }
        return -1;
    }




    public ArrayList<graspElement> Solution(Products products) {

        // Actualmente consideramos el origen como punto de inicio
        int turn_counter = 0;
        ArrayList<graspElement> graspSolution = new ArrayList<graspElement>();
        ArrayList<graspElement> graspList = new ArrayList<graspElement>();

        graspSolution.add(new graspElement(new Coord(0,0),-1,-1));

        for (int k =0; k< products.size();k++){
            graspList.add(new graspElement(products.getProduct(k),-1,k+1));
        }

        // Core del Grasp

        while (turn_counter <= products.size()+1){
            // Primera comparación
            print_solution(graspSolution);
            if (turn_counter==0 || turn_counter<products.size()){
                if (turn_counter==0){
                // Del origen al primer elemento
                    for( graspElement elem : graspList){
                        graspElement ant = new graspElement(new Coord(0,0),0,0);
                        graspElement act = new graspElement(elem.coord,elem.cost,elem.distances_index);
                        elem.cost = distance(ant,act,this.wmap.cpReal_map(),(this.wmap.n_racks(ant,act)+1)*100);
                    }
                }else if(turn_counter<=products.size()){
                    for(graspElement elem : graspList){
                        graspElement ant = graspSolution.get(turn_counter);
                        graspElement act = new graspElement(elem.coord,elem.cost,elem.distances_index);
                        elem.cost = distance(ant,act,this.wmap.cpReal_map(),(this.wmap.n_racks(ant,act)+1)*100);
                        while (elem.cost == -1){
                            System.out.print(elem.cost+ "por coord ["+act.coord.y+","+act.coord.x+"]");
                            elem.cost = distance(ant,act,this.wmap.cpReal_map(),(this.wmap.n_racks(ant,act)+1)*100);
                        }
                    }
                }
                // we sort the list
                graspList.sort((Comparator<? super graspElement>) new graspElement().cmp);

                // elegimos dentro de un rango, la mejor solucion
                Random generator = new Random();
                System.out.println("N turno: "+turn_counter);
                System.out.println("Tamanho de lista: "+graspList.size());
                int rand_int = generator.nextInt((int)(graspList.size()*alpha)+1);

                // Transfer of selected item
                graspSolution.add(graspList.remove(rand_int));

                // See new solution


                // Reseting costs on graspList
                for (graspElement elem : graspList){
                    elem.cost=-1;
                }

            }else{
                    if(turn_counter == products.size()+1){
                        graspSolution.add(new graspElement(new Coord(0,0),-1,turn_counter+1));
                        graspElement ant = graspSolution.get(turn_counter-1);
                        graspElement act = new graspElement(new Coord(0,0),0,0);
                        graspSolution.get(turn_counter).cost = distance(ant,act,this.wmap.cpReal_map(),(this.wmap.n_racks(ant,act)+1)*100);
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
