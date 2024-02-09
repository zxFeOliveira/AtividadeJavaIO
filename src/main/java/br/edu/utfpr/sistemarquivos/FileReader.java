package br.edu.utfpr.sistemarquivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path) {
        // TODO implementar a leitura dos arquivos do PATH aqui
        final String fileExtension = path.toString().substring(path.toString().length() - 4);
        if (fileExtension.equals(".txt")){
            try {
                Files.readAllLines(path).forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("This command should be used with files only");
        }
    }
}
