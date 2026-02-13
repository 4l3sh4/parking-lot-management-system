package gui;

import javax.swing.JComponent;
import model.User;

public interface Actionable
{
    public String getLabel();
    
    public void execute(User u);
    
    public JComponent getPanel();
}