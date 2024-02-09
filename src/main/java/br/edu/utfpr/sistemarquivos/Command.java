package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {

            // TODO implementar conforme enunciado
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                System.out.println("Contents of " + path);
                for (Path fileOrDirectory : stream) {
                    System.out.println(fileOrDirectory.getFileName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return path;
        }
    },
    SHOW() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) throws UnsupportedOperationException{
            // TODO implementar conforme enunciado
            final String fileName;
            final FileReader fileReader = new FileReader();

            if (parameters.length == 2) {
                fileName = parameters[1];
                final Path filePath = Paths.get(path.toString() + File.separator + fileName);
                fileReader.read(filePath);
            } else if (parameters.length == 1){
                throw new UnsupportedOperationException("Enter the file name!");
            } else {
                throw new UnsupportedOperationException("Invalid parameters");
            }

            return path;
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            final String currentDirectory = path.getFileName().toString();

            if (currentDirectory.equals("hd")){
                throw new UnsupportedOperationException("Invalid operation. You are in the root directory");
            } else {
                final Path previousDirectory = path.getParent();
                path = previousDirectory;
            }

            return path;
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            boolean directoryExists = false;
            final String directoryName;
            final List<String> directorys = new ArrayList<>();
            if (parameters.length == 2) {
                directoryName = parameters[1];

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path fileOrDirectory : stream) {
                        directorys.add(String.valueOf(fileOrDirectory.getFileName()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for ( String directory : directorys) {
                    if (directoryName.equals(directory)){
                        directoryExists = true;
                    }
                }
                if (directoryExists){

                    Path auxPath = Paths.get(path.toString() + File.separator + directoryName);
                    BasicFileAttributeView view = Files.getFileAttributeView(auxPath, BasicFileAttributeView.class);
                    if (view != null) {
                        try {
                            BasicFileAttributes attributes = view.readAttributes();
                            if (attributes.isDirectory()){
                                path = auxPath;
                            } else {
                                throw new UnsupportedOperationException("It is not a directory");
                            }
                        } catch (Exception e){
                            throw new UnsupportedOperationException("Error! Verify directory's name");
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Directory does not exist");
                }
            } else if (parameters.length == 1){
                throw new UnsupportedOperationException("Enter the directory name!");
            } else {
                throw new UnsupportedOperationException("Invalid parameters");
            }

            return path;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) {
            final String fileOrdirectoryName;
            // TODO implementar conforme enunciado
            if (parameters.length == 2) {
                final String fileOrDirectoryName = "\\" + parameters[1];
                final Path fileOrDirectoryPath = Paths.get(path.toString().concat(fileOrDirectoryName));
                System.out.println(fileOrDirectoryPath.toString());
                BasicFileAttributeView view = Files.getFileAttributeView(fileOrDirectoryPath, BasicFileAttributeView.class);
                if (view != null) {
                    try {
                        BasicFileAttributes attributes = view.readAttributes();
                        System.out.println("Is directory [" + attributes.isDirectory() + "]");
                        System.out.println("Size [" + attributes.size() + "]");
                        System.out.println("Created on [" + attributes.creationTime() + "]");
                        System.out.println("Last access time [" + attributes.lastAccessTime() + "]");
                    } catch (IOException e) {
                        throw new UnsupportedOperationException("Error while getting attributes. Verify file/directory name");
                    }
                }
            }  else if (parameters.length == 1){
                throw new UnsupportedOperationException("Enter the file or directory name!");
            } else {
                throw new UnsupportedOperationException("Invalid parameters");
            }

            return path;
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
