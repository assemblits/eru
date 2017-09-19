package com.eru.packages;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.File;

@Value
@AllArgsConstructor
public class ClassInfo {
    String name;
    String absolutePackage;
    File file;
    Class<?> clazz;
}