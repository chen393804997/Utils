package com.example.demo.exception;

public interface RestStatus {

    String code();

    /**
     * @return status enum name
     */
    String name();

    /**
     * @return message summary
     */
    String desc();
}