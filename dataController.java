import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class dataController {
    private final dataModel model;
    private final dataView view;

    //File file = new File("data.txt");

    public dataController(dataModel model, dataView view) {
        this.model = model;
        this.view = view;

        // Add action listener to the send button
        view.getSendButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Add Enter key functionality
        view.getInputField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = view.getInputField().getText();
        if(!message.trim().isEmpty()) {
            // updating the model
            model.addMessage(message);

            // update the view
            view.getChatArea().append("You: " + message + "\n");
            view.getInputField().setText("");
        }
    }

    public static void main(String[] args) {
        // Create MVC components
        dataModel model = new dataModel();
        dataView view = new dataView();

        new dataController(model, view);
        // Make the GUI visible
        view.setVisible(true);
>>>>>>> 2adbe06ffe46fcdd5617e3a3ae7bdcd121e77f22
    }
}
