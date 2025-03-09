package org.glazweq.eduportal.exeptions;

public class DuplicateFolderNameException extends RuntimeException {
    public DuplicateFolderNameException(String message) {
        super(message);
    }
}
