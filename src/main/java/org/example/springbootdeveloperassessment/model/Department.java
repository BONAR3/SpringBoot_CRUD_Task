package org.example.springbootdeveloperassessment.model;

import java.util.Map;

public class Department {

        private static final Map<String, Boolean> DEPARTMENTS = Map.of(
                "IT", true,
                "HR", true,
                "LOGISTICS", false,
                "FINANCE", false,
                "DESIGN", true
        );

        public static boolean exists(String department) {
            return DEPARTMENTS.containsKey(department.toUpperCase());
        }

        public static boolean acceptsInterns(String department) {
            return DEPARTMENTS.getOrDefault(department.toUpperCase(), false);
        }
    }
