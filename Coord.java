/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campis.dp1.models;

import static java.lang.Math.sqrt;

/**
 *
 * @author sergio
 */
public class Coord {
    public int y;
    public int x;

    public Coord(){
        super();
    }
    public Coord(int y, int x){
        this.x=x;
        this.y=y;
    }

    public Coord(Coord get) {
        this.x=get.x;
        this.y=get.y;
    }

    public void modify_c(int y,int x){
        this.y+=y;
        this.x+=x;
        return;
    }

    public boolean front_of(Coord c2){
        if ((Math.abs(this.y-c2.y) == 1 && this.x==c2.x) || (Math.abs(this.x-c2.x)==1 && this.y==c2.y)) return true;
        return false;
    }

    public int c_distance(Coord coord) {
        int op1=(this.x-coord.x)*(this.x-coord.x);
        int op2=(this.y-coord.y)*(this.y-coord.y);
        int returnable= (int)sqrt(op1+op2);
        return  returnable;
    }
    
    public void print_c(){
        System.out.print("[ "+this.y+", "+this.x+"] ");
    }
}

