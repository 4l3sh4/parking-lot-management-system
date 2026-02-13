import gui.NavigationHandler;
import storage.LoadData;
import model.User;

public class Main {

    public static void main(String[] args) {

        LoadData.loadAllData(); 
        NavigationHandler.initialize();

    }
}

