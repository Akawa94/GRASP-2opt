import java.io.*;
import java.util.Scanner;
import java.io.IOException;

public class WMap {
    private int x = 0;
    private int y = 0;
    private int[][] real_map;
    public WMap(){
        super();
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Ingrese la direccion del mapa: ");
        String filename = keyboard.nextLine();
        initialize(filename);
    }

    public void initialize(String mapPath){
        File file = new File(mapPath);
        try {
            // For reading content
            Scanner inputFile = new Scanner(file);

            // We will store them in aux array 1000x1000
            int[][] auxMap = new int[1000][1000];
            while (inputFile.hasNext()){
                String line =inputFile.nextLine();
                this.x=0;
                for(char c:line.toCharArray()){
                    auxMap[this.y][this.x]=Character.getNumericValue(c);
                    this.x+=1;
                }
                this.y+=1;
            }
            inputFile.close();

            // Logging
            // System.out.println(this.y);
            // System.out.println(this.x);

            // Build real real_map
            this.real_map = new int[this.y][this.x];
            for(int j = 0; j < this.y; j++){
                for(int i = 0; i < this.x; i++){
                    this.real_map[j][i] = auxMap[j][i];
                }
            }

            // this.show();


        }catch(IOException e){
            System.out.println("No se encontro archivo.");
            return;
        }
    }

    public int getVal(int j,int i){
        return this.real_map[j][i];
    }

    public int[] getSize(){
        int[] retornable = new int[2];
        retornable[0]=this.y;
        retornable[1]=this.x;
        return retornable;
    }

    public void show(){
        for(int j=0; j<this.y; j++){
            for(int i = 0; i < this.x; i++){
                System.out.print(real_map[j][i]);
            }
            System.out.print("\n");
        }
        return;
    }

    public int[][] cpReal_map(){
        return this.real_map.clone();
    }

    private int parallel_racks(Coord c1, Coord c2, int opt){
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
                if (this.real_map[c1.y][k]==1){
                    rack_n+=1;
                }
            }
        }else{
            for (int k = init; k<=fin ;k++){
                if (this.real_map[k][c1.x]==1){
                    rack_n+=1;
                }
            }
        }

        return rack_n;
    }

    private int n_racks_diagonal(Coord c1,Coord c2) {
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
                int val =  this.real_map[ini_y][ini_x];
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
                    int val =  this.real_map[ini_y][ini_x];
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

    private int n_racks_diff(Coord c1, Coord c2){
        int rack_n = 0;
        int diff_y = c2.y-c1.y;
        int diff_x = c2.x-c1.x;
        // first half square
        int ini_y = c1.y;
        int ini_x =c1.x;
        boolean over_one=true;
        for (int i = 0; i < Math.abs(diff_y); i++) {
            int val =  this.real_map[ini_y][ini_x];
            if (val==1 && over_one==false){
                rack_n += 1;
                over_one = true;
            }else if (val==0 && over_one==true){
                over_one=false;
            }
            ini_y+=diff_y/Math.abs(diff_y);
        }
        for (int i = 0; i < Math.abs(diff_x); i++) {
            int val =  this.real_map[ini_y][ini_x];
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
            if (this.real_map[ini_y][ini_x]==1){
                rack_n+=1;
            }
            ini_y+=diff_y/Math.abs(diff_y);
        }
        for (int i = 0; i < Math.abs(diff_x) ; i++) {
            if (this.real_map[ini_y][ini_x]==1){
                rack_n+=1;
            }
            ini_x+=diff_x/Math.abs(diff_x);
        }
        return rack_n;
    }

    public int n_racks(graspElement ge1, graspElement ge2){
        // Numero de racks entre 2 puntos
        Coord c1 = ge1.coord;
        Coord c2 = ge2.coord;
        int rack_n=0;
        // Revisamos si no encuentran en paralelo
        if (c1.y==c2.y || c1.x==c2.x){
            if (c1.y==c2.y){
                // paralelos en y
                return parallel_racks(c1,c2,0);
            }else{
                //paralelos en x
                return parallel_racks(c1,c2,1);
            }
        }else{
            // Revisamos racks en diagonal y por diff de coordenadas
            System.out.println("Entre a diagonal con las coords [" + c1.y + ", "+c1.x+"] y ["+c2.y + ", "+c2.x+"].");
            rack_n+=n_racks_diagonal(c1,c2);
            rack_n+=n_racks_diff(c1,c2);   // peso doble
            return rack_n/3;
        }
    }
}
