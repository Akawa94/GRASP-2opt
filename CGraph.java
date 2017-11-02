/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campis.dp1.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sergio
 */
public class CGraph {
    private Map<String,CNode> nodes = new HashMap<>();
    
    public void addNode(CNode cnode){
        String key="[ "+cnode.getPos().y+", "+cnode.getPos().x+"]";
        this.nodes.put(key, cnode);
    }
    
    public CNode getNode(String key){
        return this.nodes.get(key);
    }
    
    public CNode getNode(int y, int x){
        String key="[ "+y+", "+x+"]";
        return this.nodes.get(key);
    }
    
    public void delNode(String key){
        // del all occurrences of node in 
        int y = this.nodes.get(key).ubication.y;
        int x = this.nodes.get(key).ubication.x;
        for (String key_ : this.nodes.keySet()){
            CNode aux = this.nodes.get(key_);
            aux.delPath(new Coord(y,x));
        }
        // final step
        this.nodes.remove(key);
    }
    
    public Set<String> keySet(){
        return this.nodes.keySet();
    }

    public void setup(int[][] map) {
        // origin was added before this method
        // all vs all
        ArrayList<String> keys =  new ArrayList<String>(this.nodes.keySet());
        for (int i = 0; i < this.nodes.size(); i++) {
            for (int j = 0; j < this.nodes.size(); j++) {
                CNode a = this.nodes.get(keys.get(i));
                CNode b = this.nodes.get(keys.get(j));
                if (sameNode(a,b)==false && zero_racks(a.ubication,b.ubication,map)){
                    a.addPath(b.ubication); // method should check for duplicates
                    b.addPath(a.ubication);
                }
            }
        }
        for (String key : keys) {
            CNode aux = this.nodes.get(key);
            System.out.print("\nNodos conectados a [ "+aux.ubication.y+" , "+aux.ubication.x+"] son: ");
            for (Coord path:aux.paths){
                System.out.print(" [ "+path.y+", "+path.x+"],");
            }
            System.out.println(".");
        }
    }

    public int getSize() {
        return this.nodes.size();
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
    
    private boolean overbound(int ini_y, int ini_x, Coord c1, Coord c2){
        return ((ini_y>=c1.y && ini_y<=c2.y) || (ini_y>=c2.y && ini_y<=c1.y))
                && ((ini_x>=c1.x && ini_x<=c2.x) || (ini_x>=c2.x && ini_x<=c1.x));
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
                if (overbound(ini_y,ini_x,c1,c2)){
                    int val =  map[ini_y][ini_x];
                    if (val==1 && over_one==false){
                        rack_n += 1;
                        over_one = true;
                        return rack_n;
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
                        return rack_n;
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

    private boolean zero_racks(Coord c1, Coord c2, int[][] map) {
     int rack_n=0;
        // Revisamos si no encuentran en paralelo
        // System.out.println("Hemos entrado a zero_racks, con las coords: [ "+c1.y+", "+c1.x+"]  y  [ "+c2.y+", "+c2.x+"].");
        if (c1.y==c2.y || c1.x==c2.x){
            if (c1.y==c2.y){
                // paralelos en y
                if (parallel_racks(c1,c2,0,map)>0) return false;
            }else{
                //paralelos en x
                if (parallel_racks(c1,c2,1,map)>0) return false;
            }
        }else{
            // Revisamos racks en diagonal y por diff de coordenadas
            //System.out.println("Entre a diagonal con las coords [" + c1.y + ", "+c1.x+"] y ["+c2.y + ", "+c2.x+"].");
            if (n_racks_diagonal(c1,c2,map)>0) return false;
        }
        return true;
    }

    public boolean sameNode(CNode a, CNode b) {
        if (a.getPos().y == b.getPos().y && a.getPos().x == b.getPos().x)
            return true;
        return false;
    }
}
