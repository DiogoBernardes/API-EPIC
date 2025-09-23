package com.epic.finance.exception.categories;

public class ExistingCategoryException  extends RuntimeException {
    public ExistingCategoryException(String message) {
        super(message);
    }
}
