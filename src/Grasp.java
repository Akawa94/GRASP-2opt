import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.util.*;

public class Grasp {
    private int[][] distances; // grid of distances for future check
    private WMap wmap;
    private double alpha = 1;
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



    private int distance(Coord prod1, Coord prod2, int[][] map, int max_turns){
        // all in [y,x] format

        // Will advance in straight line as preferred, turns will only occur when not possible to advance

        // Directionality 'u' Up | 'd' Down | 'l' left | 'r' right





//        Random generator = new Random();
//        int i = generator.nextInt(10);


        return this.wmap.n_racks(prod1,prod2);
    }

    public Grasp(WMap wmap, double alpha){
        this.wmap=wmap;
        this.alpha=alpha;
    }

    public void Initialize(Products products){

        // Initialize distances matrix at -1
        this.distances = new int[products.size()][products.size()];
        for(int i=0;i<products.size();i++){
            for(int j=0;j<products.size();j++){
                this.distances[i][j]=-1;
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
                        Coord ant = new Coord(0,0);
                        Coord act = elem.coord;
                        elem.cost = distance(ant,act,this.wmap.cpReal_map(),this.wmap.n_racks(ant,act));
                    }
                }else if(turn_counter<products.size()){
                    for(graspElement elem : graspList){
                        Coord ant = graspSolution.get(turn_counter).coord;
                        Coord act = elem.coord;
                        elem.cost = distance(ant, act,this.wmap.cpReal_map(),this.wmap.n_racks(ant,act));
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
                        Coord ant = graspSolution.get(turn_counter).coord;
                        Coord act = new Coord(0,0);
                        graspSolution.get(turn_counter+1).cost = distance(ant,act,this.wmap.cpReal_map(),this.wmap.n_racks(ant,act));
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
