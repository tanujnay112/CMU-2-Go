package c105.com.cmu2go;

/**
 * Created by Tanuj on 2/11/17.
 */

public class Order {
    public String account;
    public String place;
    public String food;
    public String location;
    public String status;

    Order(){
        account = "";
        place = "";
        food = "";
        location = "";
        this.status = "";
    }

    Order(String a, String p, String f, String l, String status){
        account = a;
        place = p;
        food = f;
        location = l;
        this.status = status;
    }
}
