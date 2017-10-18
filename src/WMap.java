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
}
