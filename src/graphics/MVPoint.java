package graphics;

import java.io.IOException;

/**
 * Represent single point on MapView
 * numbers go from 1.
 * 1,1 is tom left corner.
 *
 * Example:<br/>
 *  a - - <br/>
 *  - - -<br/>
 *  - b -<br/>
 *<br/>
 *  a = (x = 1, y= 1)<br/>
 *  b = (x = 2, y = 3)<br/>
 *
 * */
public class MVPoint implements java.io.Serializable  {


    /** x position on mapview */
    public int x;
    /** y position on mapview */
    public int y;

    public MVPoint(int x,int y){
        this.x = x;
        this.y = y;
    }


    @Override
    public boolean equals(Object obj) {
        //return super.equals(obj);

        if (obj == null) return false;
        if (obj == this) return true;
        if (!obj.getClass().getName().equals(this.getClass().getName())){
            return false;
        }
        MVPoint mvp = (MVPoint)obj;

        if (this.x == mvp.x && this.y == mvp.y) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public int hashCode() {
        int hash = 1;
        hash = this.x*1024;
        hash = hash + this.y;
        return hash;
    }

    public int compareTo(MVPoint other){
        //negative if this is before other 0 if equals, 1 otherwise
        if (this.x < other.x) { return -1; }
        if (this.x == other.x) {
            if (this.y < other.y) { return -1; }
            if (this.y == other.y) {return 0;}
        }

        return 1;

    }

    public String toString(){
        return String.format("(%s,%s)",x,y);
    }

    /** converts string representation of mvpoint like "(5,6)" to  point representation */
    public static MVPoint mvPointFromString(String stringRep) throws IOException {
        //return String.format("(%s,%s)",x,y);
        stringRep = stringRep.replace("(","");
        stringRep = stringRep.replace(")","");
        String[] nums = stringRep.split(",");

        if (nums.length != 2){
            throw new IOException("Cannot parse Point :\" "+ stringRep + "\"");
        }
        int x=-1;int y =-1;
        try {
            x = Integer.parseInt(nums[0]);
            y = Integer.parseInt(nums[1]);

        }catch (Exception e){
            throw new IOException("Cannot parse Point :\" "+ stringRep + "\"");
        }
        if (x < 0 || y < 0) {
            throw new IOException("Cannot parse Point :\" "+ stringRep + "\"");
        }

        return new MVPoint(x,y);
    }
}
