import java.util.ArrayList;

public class Utils {
    public int total_cost(ArrayList<graspElement> list){
        int total_cost = 0;
        for (graspElement elem: list){
            if (elem.cost!=-1){
                total_cost+=elem.cost;
            }
        }
        return total_cost;
    }
    private int parallel_racks(Coord c1, Coord c2, int opt,int[][] map){
        int rack_n=0;
        int c_i;
        int c_f;
        if (opt==0){
            c_i=c1.x;
            c_f=c2.x;
        }else{
            c_i=c1.y;
            c_f=c2.y;
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
            for (int k = init; k<=fin ;k++){
                if (map[c1.y][k]==1){
                    rack_n+=1;
                }
            }
        }else{
            for (int k = init; k<=fin ;k++){
                if (map[k][c1.x]==1){
                    rack_n+=1;
                }
            }
        }

        return rack_n;
    }

    private int n_racks_diagonal(Coord c1,Coord c2,int[][] map) {
        int rack_n = 0;
        int diff_y = c1.y-c2.y;
        int diff_x = c1.x-c2.x;
        int step_y = Math.abs((diff_y)/(diff_x))==0?1:Math.abs((diff_y)/(diff_x));
        int step_x = Math.abs((diff_x)/(diff_y))==0?1:Math.abs((diff_x)/(diff_y));
        // System.out.println("Mis steps son para y: "+step_y+"  & para x: "+step_x);
        int ini_y = c1.y;
        int ini_x = c1.x;
        boolean over_one=true;
        do{
            for (int i = 0; i < Math.abs(step_y); i++) {
                int val =  map[ini_y][ini_x];
                if (overbound(ini_y,ini_x,c1,c2)){
                    if (val==1 && over_one==false){
                        rack_n += 1;
                        over_one = true;
                    }else if (val==0 && over_one==true){
                        over_one=false;
                    }
                }else{
                    break;
                }
                // System.out.println("Coordenada ["+ini_y+", "+ini_x+"].");
                ini_y+=(-1)*diff_y/Math.abs(diff_y);
            }
            for (int i = 0; i < Math.abs(step_x); i++) {
                if (overbound(ini_y,ini_x,c1,c2)){
                    int val =  map[ini_y][ini_x];
                    if (val==1 && over_one==false){
                        rack_n += 1;
                        over_one = true;
                    }else if (val==0 && over_one==true){
                        over_one=false;
                    }
                }else{
                    break;
                }
                // System.out.println("Coordenada ["+ini_y+", "+ini_x+"].");
                ini_x+=(-1)*diff_x/Math.abs(diff_x);
            }
        }while(overbound(ini_y,ini_x,c1,c2));
        return rack_n;
    }

    private boolean overbound(int ini_y, int ini_x, Coord c1, Coord c2){
        return ((ini_y>=c1.y && ini_y<=c2.y) || (ini_y>=c2.y && ini_y<=c1.y))
                && ((ini_x>=c1.x && ini_x<=c2.x) || (ini_x>=c2.x && ini_x<=c1.x));
    }

    private int n_racks_diff(Coord c1, Coord c2,int[][] map){
        int rack_n = 0;
        int diff_y = c2.y-c1.y;
        int diff_x = c2.x-c1.x;
        // first half square
        int ini_y = c1.y;
        int ini_x =c1.x;
        boolean over_one=true;
        for (int i = 0; i < Math.abs(diff_y); i++) {
            int val =  map[ini_y][ini_x];
            if (val==1 && over_one==false){
                rack_n += 1;
                over_one = true;
            }else if (val==0 && over_one==true){
                over_one=false;
            }
            ini_y+=diff_y/Math.abs(diff_y);
        }
        for (int i = 0; i < Math.abs(diff_x); i++) {
            int val =  map[ini_y][ini_x];
            if (val==1 && over_one==false){
                rack_n += 1;
                over_one = true;
            }else if (val==0 && over_one==true){
                over_one=false;
            }
            ini_x+=diff_x/Math.abs(diff_x);
        }

        // second half square
        ini_y = c1.y;
        ini_x =c1.x;
        for (int i = 0; i < Math.abs(diff_y) ; i++) {
            if (map[ini_y][ini_x]==1){
                rack_n+=1;
            }
            ini_y+=diff_y/Math.abs(diff_y);
        }
        for (int i = 0; i < Math.abs(diff_x) ; i++) {
            if (map[ini_y][ini_x]==1){
                rack_n+=1;
            }
            ini_x+=diff_x/Math.abs(diff_x);
        }
        return rack_n;
    }

    public int n_racks(Coord c1, Coord c2,int[][] map){
        // Numero de racks entre 2 puntos

        int rack_n=0;
        // Revisamos si no encuentran en paralelo
        if (c1.y==c2.y || c1.x==c2.x){
            if (c1.y==c2.y){
                // paralelos en y
                return parallel_racks(c1,c2,0,map);
            }else{
                //paralelos en x
                return parallel_racks(c1,c2,1,map);
            }
        }else{
            // Revisamos racks en diagonal y por diff de coordenadas
            //System.out.println("Entre a diagonal con las coords [" + c1.y + ", "+c1.x+"] y ["+c2.y + ", "+c2.x+"].");
            rack_n+=n_racks_diagonal(c1,c2,map);
            rack_n+=n_racks_diff(c1,c2,map);   // peso doble
            return rack_n/3;
        }
    }
}
