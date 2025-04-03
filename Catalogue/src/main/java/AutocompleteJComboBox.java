import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A custom JComboBox that provides autocomplete functionality based on a given searchable data source.
 * This component listens for user input and dynamically updates the dropdown list with matching suggestions.
 *
 */
public class AutocompleteJComboBox extends JComboBox<String> {
    
    /**
     * The searchable data source used to find matching suggestions based on user input.
     */
    private final Searchable<String, String> searchable;

    /**
     * Constructs an AutocompleteJComboBox with the specified searchable data source.
     *
     * @param searchable The searchable implementation that provides autocomplete suggestions.
     * @throws IllegalStateException if the editor component is not a JTextComponent.
     */
    public AutocompleteJComboBox(Searchable<String, String> searchable) {
        super();
        this.searchable = searchable;
        setEditable(true);
        Component c = getEditor().getEditorComponent();
        
        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;
            final int DEBOUNCE_DELAY = 300;
            final Timer debounceTimer = new Timer(DEBOUNCE_DELAY, null);
            debounceTimer.setRepeats(false);
            final String[] lastQuery = { "" };

            // Adds a document listener to handle text changes and trigger updates.
            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }
                
                @Override
                public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }
                
                @Override
                public void changedUpdate(DocumentEvent e) { }

                /**
                 * Schedules an update of suggestions with a debounce mechanism to avoid excessive queries.
                 */
                private void scheduleUpdate() {
                    debounceTimer.stop();
                    debounceTimer.removeActionListener(debounceTimer.getActionListeners().length > 0 ? debounceTimer.getActionListeners()[0] : null);
                    debounceTimer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            updateSuggestions();
                        }
                    });
                    debounceTimer.start();
                }

                /**
                 * Updates the dropdown list with search results matching the current input.
                 */
                private void updateSuggestions() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String currentText = tc.getText();
                            
                            // Only update if the text has changed.
                            if (currentText.equals(lastQuery[0])) {
                                return;
                            }
                            lastQuery[0] = currentText;
                            int caretPos = tc.getCaretPosition();

                            // Retrieve and sort search results.
                            List<String> founds = new ArrayList<>(searchable.search(currentText));
                            Collections.sort(founds);

                            // Update combo box items without disrupting user input.
                            removeAllItems();
                            boolean containsCurrent = false;
                            
                            for (String s : founds) {
                                if (s.equalsIgnoreCase(currentText)) {
                                    containsCurrent = true;
                                    break;
                                }
                            }
                            
                            if (!containsCurrent) {
                                addItem(currentText);
                            }
                            
                            for (String s : founds) {
                                if (!s.equalsIgnoreCase(currentText)) {
                                    addItem(s);
                                }
                            }
                            
                            setPopupVisible(getItemCount() > 0);
                            tc.setText(currentText);
                            tc.setCaretPosition(Math.min(caretPos, currentText.length()));
                            tc.requestFocusInWindow();
                        }
                    });
                }
            });

            // Displays suggestions when the text field gains focus.
            tc.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (tc.getText().length() > 0) {
                        setPopupVisible(true);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) { }
            });
        } else {
            throw new IllegalStateException("Editor component is not a JTextComponent!");
        }
    }
}
