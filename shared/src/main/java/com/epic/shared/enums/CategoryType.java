package com.epic.shared.enums;

public enum CategoryType {
    Income,
    Expense;

    @Override
    public String toString() {
        return name();
    }
}
