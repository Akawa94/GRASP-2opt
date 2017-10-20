import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Route {
    ArrayList<Coord> esquinas=new ArrayList<Coord>();
    int cost=0;
    public Route(){
        super();
    }

    public Route(Route route, Coord c1, Coord c2, int[][] map) {
        // copying initial data
        super();
        for (Coord c : route.esquinas){
            this.esquinas.add(c);
        }
        this.cost = route.cost;
        // validating that route is not complete already
        if (validate_route(route.esquinas.get(route.esquinas.size()-1),c2,map)) return;
        // creating three lines over the map
        Coord c_i = c1;
        for (int i = 0; i < 3; i++) {
            int direccion = dir_entre_coords(c_i,c2);
//            0   1   2
//            7   c   3
//            6   5   4
            Coord c_n = new Coord();
            c_n=advance(-1,c_i,direccion,map);
            this.cost+=(Math.abs(c_n.y-c_i.y) + Math.abs(c_n.x-c_i.x)); // avanzo en alguna dir_entre_coords
            this.esquinas.add(c_n);
            c_i=c_n;
        }



    }

    private boolean validate_route(Coord c1, Coord c2, int[][] map) {
        if (c1.y==c2.y){
            int diff_x = Math.abs(c1.x-c2.x);
            for (int i = 0; i < diff_x; i++) {
                if (map[c1.y][c1.x+i*((c1.x-c2.x)/diff_x)]==1) return false;
            }
            if (diff_x>4) return false;
        }else{
            int diff_y = Math.abs(c1.y-c2.y);
            for (int i = 0; i < diff_y; i++) {
                if (map[c1.y][c1.x+i*((c1.y-c2.y)/diff_y)]==1) return false;
            }
            if (diff_y>4) return false;
        }
        return true;

    }

    private boolean validate_dir(int dir, Coord c1, int[][] map){
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda
        int Y=0;
        int X=0;
        for (int i = 0; ; i++) {
            X=0;
            for (int j = 0; ; j++) {
                X+=1;
                try{
                    int hola = map[Y][X]-1;
                }catch(Exception e){
                    break;
                }
            }
            Y+=1;
            try{
                int hola = map[Y][X]-1;
            }catch(Exception e){
                break;
            }
        }

        switch (dir){
            case 0:
                for (int i = 0; i < 4; i++) {
                    if ((c1.y-i)<=0) return false;
                    if (map[c1.y-i][c1.x]==1) return false;
                }
                return true;
            case 1:
                for (int i = 0; i < 4; i++) {
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
                    if ((c1.x+i)<=0) return false;
                    if (map[c1.y-1][c1.x]==1) return false;
                }
                return true;
        }
        return false;
    }


    private Coord advance(int last_dir,Coord c_i, int direccion, int[][] map) {
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda
        int dir_avanzar = dir_rand(last_dir,c_i,direccion,map);
        // Avanzaremos hasta que consigamos alguna posibilidad de giro

        int counter_y=0;
        int counter_x=0;
        int y_bonus = (dir_avanzar==0?-1:0)+(dir_avanzar==2?1:0);
        int x_bonus = (dir_avanzar==1?1:0)+(dir_avanzar==3?-1:0);
        int n_steps_obs_p=0;
        int n_steps_obs_n=0;
        if (dir_avanzar==1 || dir_avanzar==3){
            int aux_steps_y=0;
            for (int i = 0;; i++) {
                try {
                    if ((map[c_i.y + i][c_i.x] == 1)) break;
                    aux_steps_y += 1;
                }catch (Exception e){
                    aux_steps_y=0;
                    break;
                }
            }
            if (aux_steps_y>n_steps_obs_p) n_steps_obs_p=aux_steps_y;
            aux_steps_y=0;
            for (int i = 0;; i++) {
                try {
                    if ((map[c_i.y - i][c_i.x] == 1)) break;
                    aux_steps_y += 1;
                }catch (Exception e){
                    aux_steps_y=0;
                    break;
                }
            }
            if (aux_steps_y>n_steps_obs_n) n_steps_obs_n=aux_steps_y;
            while (map[c_i.y+counter_y+y_bonus][c_i.x+counter_x+x_bonus] != 1){
                counter_y+=y_bonus;
                counter_x+=x_bonus;
                if (n_steps_obs_p!=0 && map[c_i.y+counter_y+n_steps_obs_p+1][c_i.x+counter_x]==0) break;
                if (n_steps_obs_n!=0 && map[c_i.y+counter_y-n_steps_obs_n-1][c_i.x+counter_x]==0) break;
            }
        }else{
            int aux_steps_x=0;
            for (int i = 0;; i++) {
                try {
                    if ((map[c_i.y][c_i.x + i] == 1)) break;
                    aux_steps_x += 1;
                }catch (Exception e){
                    aux_steps_x=0;
                    break;
                }
            }
            if (aux_steps_x>=n_steps_obs_p) n_steps_obs_p=aux_steps_x;
            aux_steps_x=0;
            for (int i = 0;; i++) {
                try {
                    if ((map[c_i.y][c_i.x - i] == 1)) break;
                    aux_steps_x += 1;
                }catch (Exception e){
                    aux_steps_x=0;
                    break;
                }
            }
            if (aux_steps_x>=n_steps_obs_n) n_steps_obs_n=aux_steps_x;
            while (map[c_i.y+counter_y+y_bonus][c_i.x+counter_x+x_bonus] != 1){
                counter_y+=y_bonus;
                counter_x+=x_bonus;
                if (n_steps_obs_p!=0 && map[c_i.y+counter_y][c_i.x+counter_x+n_steps_obs_p]==0) break;
                if (n_steps_obs_p!=0 && map[c_i.y+counter_y][c_i.x+counter_x-n_steps_obs_n]==0) break;
            }
        }
        Coord retornable = new Coord(c_i.y+counter_y,c_i.x+counter_x);
        return retornable;
    }

    private int dir_rand(int last_dir, Coord c_i, int direccion, int[][] map) {
        int[] dir_posibles = new int[4];
        // 0 arriba
        // 1 derecha
        // 2 abajo
        // 3 izquierda
        for (int i = 0; i < 4; i++) {
            if (validate_dir(i,c_i,map) && i!=last_dir) dir_posibles[i]=4;
            else dir_posibles[i]=0;
        }

        switch(direccion){
            case 0: dir_posibles[1]*=0.75;
                dir_posibles[2]*=0.75;
                break;
            case 1: dir_posibles[2]*=0.75;
                break;
            case 2: dir_posibles[2]*=0.75;
                dir_posibles[3]*=0.75;
                break;
            case 3: dir_posibles[3]*=0.75;
                break;
            case 4: dir_posibles[0]*=0.75;
                dir_posibles[3]*=0.75;
                break;
            case 5: dir_posibles[0]*=0.75;
                break;
            case 6: dir_posibles[0]*=0.75;
                dir_posibles[1]*=0.75;
                break;
            case 7: dir_posibles[1]*=0.75;

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
        int diff_y=c2.y-c1.y;
        int diff_x=c2.x=c1.x;

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


    int partition(int[][] arr, int low, int high)
    {
        int[] pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j][1] <= pivot[1])
            {
                i++;

                // swap arr[i] and arr[j]
                int[] temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        int[] temp = arr[i+1];
        arr[i+1] = arr[high];
        arr[high] = temp;

        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    void qs_sort(int[][] arr, int low, int high)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            qs_sort(arr, low, pi-1);
            qs_sort(arr, pi+1, high);
        }
    }

}
