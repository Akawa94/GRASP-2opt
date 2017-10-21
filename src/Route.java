import java.util.ArrayList;
import java.util.Random;

public class Route {
    ArrayList<Coord> esquinas=new ArrayList<Coord>();
    int cost=0;

    public Route(){
        super();
    }

    public Route(Route cp){
        super();
        for (Coord c : cp.esquinas){
            this.esquinas.add(c);
        }
        this.cost=cp.cost;
    }

    public Route(Route route, Coord c1, Coord c2, int[][] map) {
        // copying initial data
        super();
        for (Coord c : route.esquinas){
            this.esquinas.add(c);
        }
        this.cost = route.cost;
        // validating that route is not complete already
        if (route.esquinas.size()>0 && route.esquinas.get(route.esquinas.size()-1).front_of(c2)) return;
        // creating three lines over the map
        Coord c_iterate = new Coord(c1.y,c1.x);
        for (int i = 0; i < 2; i++) {
            if (c_iterate.front_of(c2)) break;
            int direccion = dir_entre_coords(c_iterate,c2);
//            0   1   2
//            7   c   3
//            6   5   4
            Coord c_result;
            c_result=advance(-1,c_iterate,c2,direccion,map);
            Coord c_n=new Coord(c_result.y,c_result.x);
            this.cost+=(Math.abs(c_n.y-c_iterate.y) + Math.abs(c_n.x-c_iterate.x)); // avanzo en alguna dir_entre_coords
            this.esquinas.add(c_n);
            c_iterate = new Coord(c_n.y,c_n.x);
        }
    }

    public boolean validate_advance(Coord c1, Coord c2, int[][] map) {

        if (Math.abs(c1.x-c2.x)==1 || Math.abs(c1.y-c2.y)==1) return true;
        else return false;
    }

    private int[] calculate_size(int[][] map){
        int X,Y;
        Y=0;
        for (int i = 0; ; i++) {
            X=0;
            for (int j = 0; ; j++) {
                try{
                    int hola = map[Y][X+1]-1;
                }catch(Exception e){
                    break;
                }
                X+=1;
            }
            try{
                int hola = map[Y+1][X]-1;
            }catch(Exception e){
                break;
            }
            Y+=1;
        }
        int[] retornable = new int[2];
        retornable[0]=Y;
        retornable[1]=X;

        return retornable;
    }

    private boolean validate_dir(int dir, Coord c1, int[][] map){
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda
        int Y=0;
        int X=0;
        int[] size = new int[2];
        size = calculate_size(map);
        Y=size[0];
        X=size[1];

        switch (dir){
            case 0:
                for (int i = 0; i < 4; i++) {
                    if ((c1.y-i)<=0) return false;
                    if (map[c1.y-i][c1.x]==1) return false;
                }
                return true;
            case 1:
                for (int i = 0; i < 4; i++) {
                    //System.out.println(X+Y);
                    if ((c1.x+i)>=X) return false;
                    if (map[c1.y][c1.x+1]==1) return false;
                }
                return true;
            case 2:
                for (int i = 0; i < 4; i++) {
                    if ((c1.y+i)>=Y) return false;
                    if (map[c1.y+i][c1.x]==1) return false;
                }
                return true;
            case 3:
                for (int i = 0; i < 4; i++) {
                    if ((c1.x-i)<=0) return false;
                    if (map[c1.y][c1.x-i]==1) return false;
                }
                return true;
        }
        return false;
    }


    private Coord advance(int last_dir,Coord c_begin, Coord c_destiny, int direccion, int[][] map) {
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda
        // si se encuentra frente al producto, con cero racks, ira hacia el
        int dir_avanzar = dir_rand(last_dir,c_begin,c_destiny,direccion,map);
        Utils utils = new Utils();
        int init_n_racks = utils.n_racks(c_begin,c_destiny,map);
        graspElement best_opt=new graspElement(new Coord(c_begin.y,c_begin.x),-1,-1);
        if (init_n_racks==1 && (c_begin.y==c_destiny.y || c_begin.x==c_destiny.x)){

            if (c_begin.y==c_destiny.y){
                if (c_begin.x-c_destiny.x<0){
                    best_opt=new graspElement(new Coord(c_begin.y,c_destiny.x-1),-1,-1);
                }else{
                    best_opt=new graspElement(new Coord(c_begin.y,c_destiny.x+1),-1,-1);
                }
            }else{
                if (c_begin.y-c_destiny.y<0){
                    best_opt=new graspElement(new Coord(c_destiny.y-1,c_begin.x),-1,-1);
                }else{
                    best_opt=new graspElement(new Coord(c_destiny.y+1,c_begin.x),-1,-1);
                }
            }
            return  best_opt.coord;
        }

        // Avanzaremos hasta que consigamos alguna posibilidad de giro
        // evaluara n_racks entre punto destino y punto tentativo de avance

        int X,Y;
        int[] sizes = calculate_size(map);
        Y=sizes[0];
        X=sizes[1];

        int counter_y=0;
        int counter_x=0;

        int y_bonus=0;
        if (dir_avanzar==0){
            y_bonus=-1;
        }else if (dir_avanzar==2){
            y_bonus=1;
        }
        int x_bonus = 0;
        if (dir_avanzar==1){
            x_bonus=1;
        }else if (dir_avanzar==3){
            x_bonus=-1;
        }


        Coord c_iterator = new Coord(c_begin.y,c_begin.x);
        if (y_bonus!=0) { // avanzando en Y
            // revisamos si puede avanzar
            while (c_iterator.y + y_bonus >= 0 && c_iterator.y + y_bonus < Y && map[c_iterator.y][c_iterator.x]==0) {
                c_iterator.modify_c(y_bonus,0);
                int cost_iterator = utils.n_racks(c_iterator, c_destiny, map);
                // System.out.println("Atravesando coordenadas y,x: " +c_iterator.y+", "+c_iterator.x+" con costo: "+cost_iterator+" para llegar a: [ "+c_destiny.y+", "+c_destiny.x+"].");
                if (best_opt.cost == -1 || best_opt.cost >= cost_iterator) {
                    best_opt = new graspElement(new Coord(c_iterator.y, c_iterator.x),cost_iterator,-1);
                    // System.out.println("y de best_opt:\t"+best_opt.coord.y);
                    // System.out.println("y de destino:\t"+c_destiny.y);
                    // System.out.println("costo bestopt:\t"+best_opt.cost);
                    if (c_destiny.y == best_opt.coord.y && best_opt.cost==1) return best_opt.coord;
                }
            }
        }else{  // avanzando en X
            // revisamos si puede avanzar
            while (c_iterator.x + x_bonus >= 0 && c_iterator.x + x_bonus < X && map[c_iterator.y][c_iterator.x]==0) {
                c_iterator.modify_c(0,x_bonus);
                int cost_iterator = utils.n_racks(c_iterator, c_destiny, map);
                //System.out.println("Atravesando coordenadas y,x: " +c_iterator.y+", "+c_iterator.x+" con costo: "+cost_iterator+" para llegar a: [ "+c_destiny.y+", "+c_destiny.x+"].");
                if (best_opt.cost == -1 || best_opt.cost >= cost_iterator) {
                    best_opt = new graspElement(new Coord(c_iterator.y, c_iterator.x),cost_iterator,-1);
                    // System.out.println("y de best_opt:\t"+best_opt.coord.y);
                    // System.out.println("y de destino:\t"+c_destiny.y);
                    // System.out.println("costo bestopt:\t"+best_opt.cost);
                    if (c_destiny.x == best_opt.coord.x && best_opt.cost==1) return best_opt.coord;
                }
            }
        }

        Coord retornable = new Coord(best_opt.coord.y,best_opt.coord.x);
        return retornable;
    }

    private int dir_rand(int last_dir,Coord c_begin,Coord c_destiny, int direccion, int[][] map) {
        int[] dir_posibles = new int[4];
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda

        // formato de direccion
        // 0 1 2
        // 7 c 3
        // 6 5 4
        Utils utils = new Utils();
        int n_racks = utils.n_racks(c_begin,c_destiny,map);
        if (n_racks == 0 && (direccion == 1 || direccion==3 || direccion == 5 || direccion ==7)) return (direccion-1)/2;
        for (int i = 0; i < 4; i++) {
            if (validate_dir(i,c_begin,map) && i!=last_dir) dir_posibles[i]=4;
            else dir_posibles[i]=0;
        }
        double diminish=0.75;
        switch(direccion){
            case 0: dir_posibles[1]*=diminish;
                dir_posibles[2]*=diminish;
                break;
            case 1: dir_posibles[2]*=diminish;
                break;
            case 2: dir_posibles[2]*=diminish;
                dir_posibles[3]*=diminish;
                break;
            case 3: dir_posibles[3]*=diminish;
                break;
            case 4: dir_posibles[0]*=diminish;
                dir_posibles[3]*=diminish;
                break;
            case 5: dir_posibles[0]*=diminish;
                break;
            case 6: dir_posibles[0]*=diminish;
                dir_posibles[1]*=diminish;
                break;
            case 7: dir_posibles[1]*=diminish;

        }

        int total=0;
        for (int i = 0; i < dir_posibles.length; i++) {
            total+=dir_posibles[i];
        }

        Random generator = new Random();
        int k = generator.nextInt(total+1); // random direction
        int suma_ant=0;
        for (int i = 0; i < 4; i++) {
            if (dir_posibles[i]!=0 && k<=(dir_posibles[i]+suma_ant)) return i;
            suma_ant+=dir_posibles[i];
        }
        return -1;
    }

    private int dir_entre_coords(Coord c1, Coord c2) {
        // 0 1 2
        // 7 c 3
        // 6 5 4
        int diff_y=c2.y-c1.y;
        int diff_x=c2.x-c1.x;

        if (diff_y>0){
            if (diff_x>0) return 4;
            if (diff_x==0) return 5;
            if (diff_x<0) return 6;
        }else if (diff_y==0){
            if (diff_x>0) return 3;
            if (diff_x<0) return 7;
        }else{
            if (diff_x>0) return 2;
            if (diff_x==0) return 1;
            if (diff_x<0) return 0;
        }
        return -1;
    }

    void print_map(int[][] map){
        int[] sizes = calculate_size(map);
        for(int j=0; j<sizes[0]; j++){
            for(int i = 0; i < sizes[1]; i++){
                System.out.print(map[j][i]);
            }
            System.out.print("\n");
        }
        return;
    }

}
