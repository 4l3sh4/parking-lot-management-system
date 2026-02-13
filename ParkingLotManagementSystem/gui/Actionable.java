package gui;

import javax.swing.JPanel;
import model.User;

public interface Actionable
{
    public String getLabel();
    
    public void execute(User u);
    
    public JPanel getPanel();
}