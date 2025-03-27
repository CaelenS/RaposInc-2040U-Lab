import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutocompleteJComboBox extends JComboBox<String> {
    private final Searchable<String,String> searchable;

    public AutocompleteJComboBox(Searchable<String,String> searchable) {
        super();
        this.searchable = searchable;
        setEditable(true);
        Component c = getEditor().getEditorComponent();
        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;
            // Listen to changes on the text component.
            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { update(); }
                @Override
                public void removeUpdate(DocumentEvent e) { update(); }
                @Override
                public void changedUpdate(DocumentEvent e) {}
                
                public void update(){
                    // Dispatch the update so document changes finish.
                    SwingUtilities.invokeLater(new Runnable(){
                        @Override
                        public void run() {
                            List<String> found = new ArrayList<>(searchable.search(tc.getText()));
                            // Remove duplicates ignoring case.
                            Set<String> foundSet = new HashSet<>();
                            for (String s : found) {
                                foundSet.add(s.toLowerCase());
                            }
                            Collections.sort(found);
                            // Temporarily suspend editing to update the model.
                            setEditable(false);
                            removeAllItems();
                            // If the current text isn’t in the results, add it.
                            if (!foundSet.contains(tc.getText().toLowerCase())) {
                                addItem(tc.getText());
                            }
                            for (String item : found) {
                                addItem(item);
                            }
                            setEditable(true);
                            setPopupVisible(true);
                        }
                    });
                }
            });
            
            // When focus is gained, show the popup if text exists.
            tc.addFocusListener(new FocusListener(){
                @Override
                public void focusGained(FocusEvent e) {
                    if (tc.getText().length() > 0) {
                        setPopupVisible(true);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {}
            });
        } else {
            throw new IllegalStateException("Editor component is not a JTextComponent!");
        }
    }
}