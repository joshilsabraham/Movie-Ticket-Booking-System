package main;

import controller.MainController;
import model.MovieDAO;
import model.MovieDAOImpl;
import view.MainView;
import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainView view = new MainView();
                    new MainController(view);
                    view.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Could not start application.\nError: " + e.getMessage(), 
                        "Fatal Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
