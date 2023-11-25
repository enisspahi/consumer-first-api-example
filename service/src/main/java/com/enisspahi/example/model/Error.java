package com.enisspahi.example.model;

public record Error(ErrorCode code, String message) {

    public enum ErrorCode { UNEXPECTED, NOT_FOUND }
}

