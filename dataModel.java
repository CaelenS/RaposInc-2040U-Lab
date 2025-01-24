import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class dataModel {

    public void edit(int rowNum, String customer, File file) {
        try{
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            List<String> lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            if (rowNum >= 0 && rowNum < lines.size()) {
                lines.set(rowNum, customer);

            } else {
                System.out.println("Row number out of range.");
                bw.close();
                return;
            }

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();

            System.out.println("Row edited successfully.");

            bw.close();

        } catch(IOException ioe) {
            System.out.println("Exception Occured");
        }
    }

    public void add(String customer, File file) {
        try{
            FileWriter fw = new FileWriter(file, true);

            fw.write(customer);

            fw.close();
            
        } catch(IOException ioe) {
            System.out.println("Exception Occured");
        }
    }

    public void delete(int rowNum, File file) {
        try{
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            List<String> lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            if (rowNum >= 0 && rowNum < lines.size()) {
                lines.remove(rowNum);
            } else {
                System.out.println("Row number out of range.");
                bw.close();
                return;
            }

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();

            System.out.println("Row deleted successfully.");
            
        } catch(IOException ioe) {
            System.out.println("Exception Occured");
        }
    }
}