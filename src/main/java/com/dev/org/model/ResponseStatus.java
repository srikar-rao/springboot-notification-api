package com.dev.org.model;

import lombok.Builder;

@Builder
public record ResponseStatus(boolean isSuccess, String message, String detailMessage) {}
