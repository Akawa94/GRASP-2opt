import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Products {
    private Coord[] coords;

    private int occupied(char[] array){
        int counter = 0;
        for(int i=0; i<array.length;i++){
            // null for char lol
            // System.out.println(array[i]);
            if (array[i] != '\u0000'){
                counter+=1;
            }
        }
        return counter;
    }

    private void print_array(char[] array){
        for(int i=0;i<array.length;i++){
            System.out.print(array[i]);
        }
        System.out.print('\n');
    }

    public Products(){
        super();
        Scanner keyboard = new Scanner(System.in);
        System.out.println();
//        System.out.print("Ingrese la direccion de los productos: ");
//        String filename = keyboard.nextLine();
        String filename="/media/Multimedia/Projects/GitProjects/GRASP-OPT2/Inputs/coordinates_0.txt";
        initialize(filename);
    }

    public Products(Coord[] coords){
        this.coords = new Coord[coords.length];

        this.coords = coords.clone();
    }

    public void initialize(String filepath){
        File productsPath = new File(filepath);
        try{
            // Read content
            Scanner inputFile = new Scanner(productsPath);

            // Aux array
            Coord[] aux_c = new Coord[1000];


            int number_prods=0;
            int line_counter=0;
            while(inputFile.hasNext()){
                char[] line = inputFile.nextLine().toCharArray();
                int index = 0;
                char[] j_buffer = new char[10];
                char[] i_buffer = new char[10];
                for(int k=0; k< j_buffer.length;k++){
                    i_buffer[k]='\u0000';
                    j_buffer[k]='\u0000';
                }
                System.out.println("Linea n: " + line_counter);
                line_counter+=1;
//                System.out.println("Inicializacion de buffers: ");
//                System.out.println(occupied(j_buffer));
//                System.out.println(occupied(i_buffer));
//                System.out.println("Final de inicializacion de buffers.");

                // Reading coords
                while (line[index] != ']'){
                    if (line[index] != ' ' && line[index]!= '[' && line[index] != ','){
                        j_buffer[occupied(j_buffer)]=line[index];
                        print_array(j_buffer);
                    }else if(line[index] == ','){
                        index+=1;
                        while(line[index] != ']'){
                            if (line[index] != ' '){
                                i_buffer[occupied(i_buffer)]=line[index];
                                print_array(i_buffer);
                            }
                            index+=1;
                        }
                        index-=1;
                    }
                    index+=1;
                }

                // Parsing coords
                aux_c[number_prods]=new Coord(Integer.parseInt(new String(Arrays.copyOfRange(j_buffer,0,occupied(j_buffer)))),
                Integer.parseInt(new String(Arrays.copyOfRange(i_buffer,0,occupied(i_buffer)))));
                number_prods+=1;

            }

            // As the file is all read
            this.coords = new Coord[number_prods];
            for (int k = 0; k<number_prods;k++){
                this.coords[k] = aux_c[k];
            }
        }catch (IOException e){
            System.out.println("No se encontro archivo.");
            return;
        }

    }

    public Coord getProduct(int i){
        if (i >= coords.length)
            return null;
        return this.coords[i];
    }

    public void show(){
        for(int i=0; i<this.coords.length;i++){
            System.out.println("The coords for the " + "product " + (i+1) + " are:\t [" + coords[i].y + ", " + coords[i].x + "].");
        }
    }

    public int size(){
        return coords.length;
    }

}
