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
        return (int) ((this.x-coord.x)^2+(this.y-coord.y)^2)^(1/2);
    }
}
