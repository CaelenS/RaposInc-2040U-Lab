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
            final int DEBOUNCE_DELAY = 300;
            final Timer debounceTimer = new Timer(DEBOUNCE_DELAY, null);
            debounceTimer.setRepeats(false);
            final String[] lastQuery = {""};

            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }
                @Override
                public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }
                @Override
                public void changedUpdate(DocumentEvent e) { }

                private void scheduleUpdate() {
                    debounceTimer.stop();
                    debounceTimer.removeActionListener(debounceTimer.getActionListeners().length > 0 ? debounceTimer.getActionListeners()[0] : null);
                    debounceTimer.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            updateSuggestions();
                        }
                    });
                    debounceTimer.start();
                }

                private void updateSuggestions() {
                    SwingUtilities.invokeLater(new Runnable(){
                        @Override
                        public void run() {
                            String currentText = tc.getText();
                            // Only update if the text has actually changed.
                            if (currentText.equals(lastQuery[0])) {
                                return;
                            }
                            lastQuery[0] = currentText;
                            int caretPos = tc.getCaretPosition();

                            // Retrieve suggestions based on current text.
                            List<String> founds = new ArrayList<>(searchable.search(currentText));
                            Collections.sort(founds);

                            // Update combo box without interrupting user input.
                            removeAllItems();
                            // Ensure the current text is always present.
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
                                // Avoid duplicate current entry.
                                if (!s.equalsIgnoreCase(currentText)) {
                                    addItem(s);
                                }
                            }
                            setPopupVisible(getItemCount() > 0);
                            // Restore text and caret.
                            tc.setText(currentText);
                            tc.setCaretPosition(Math.min(caretPos, currentText.length()));
                            // Ensure focus remains.
                            tc.requestFocusInWindow();
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