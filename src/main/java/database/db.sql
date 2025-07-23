-- 1. Create database and use it
CREATE DATABASE IF NOT EXISTS nutrientDB;
USE nutrientDB;

-- 2. Independent tables first
CREATE TABLE nutrient (
                          id INT PRIMARY KEY,
                          code VARCHAR(20),
                          symbol VARCHAR(20),
                          unit VARCHAR(20),
                          name VARCHAR(255),
                          name_french VARCHAR(255),
                          tagname VARCHAR(50),
                          decimals INT
);

CREATE TABLE measure_name (
                              id INT PRIMARY KEY,
                              description TEXT,
                              description_f TEXT
);

CREATE TABLE refuse_name (
                             id INT PRIMARY KEY,
                             description TEXT,
                             description_f TEXT
);

CREATE TABLE food_group (
                            id INT PRIMARY KEY,
                            code VARCHAR(20),
                            name VARCHAR(255),
                            name_french VARCHAR(255)
);

CREATE TABLE food_source (
                             id INT PRIMARY KEY,
                             code VARCHAR(20),
                             description TEXT,
                             description_f TEXT
);

CREATE TABLE nutrient_source (
                                 id INT PRIMARY KEY,
                                 code VARCHAR(10),
                                 description TEXT,
                                 description_f TEXT
);

CREATE TABLE yield_name (
                            id INT PRIMARY KEY,
                            description TEXT,
                            description_f TEXT
);

-- 3. Tables that depend on above
CREATE TABLE food_name (
                           id INT PRIMARY KEY,
                           code VARCHAR(20),
                           food_group_id INT,
                           food_source_id INT,
                           description VARCHAR(255),
                           description_french VARCHAR(255),
                           date_entry DATE,
                           date_publication DATE,
                           country_code VARCHAR(255),
                           scientific_name VARCHAR(255),
                           FOREIGN KEY (food_group_id) REFERENCES food_group(id),
                           FOREIGN KEY (food_source_id) REFERENCES food_source(id)
);

-- 4. Tables with dependencies on food_name and others
CREATE TABLE conversion_factor (
                                   food_id INT,
                                   measure_id INT,
                                   factor_value DOUBLE,
                                   date_of_entry TEXT,
                                   PRIMARY KEY (food_id, measure_id),
                                   FOREIGN KEY (food_id) REFERENCES food_name(id),
                                   FOREIGN KEY (measure_id) REFERENCES measure_name(id)
);

CREATE TABLE nutrient_amount (
                                 food_id INT,
                                 nutrient_id INT,
                                 value DOUBLE,
                                 std_error DOUBLE,
                                 num_observations INT,
                                 source_id INT,
                                 date_of_entry DATE,
                                 PRIMARY KEY (food_id, nutrient_id),
                                 FOREIGN KEY (food_id) REFERENCES food_name(id),
                                 FOREIGN KEY (nutrient_id) REFERENCES nutrient(id),
                                 FOREIGN KEY (source_id) REFERENCES nutrient_source(id)
);

CREATE TABLE refuse_amount (
                               food_id INT,
                               refuse_id INT,
                               amount DOUBLE,
                               date_of_entry TEXT,
                               PRIMARY KEY (food_id, refuse_id),
                               FOREIGN KEY (food_id) REFERENCES food_name(id),
                               FOREIGN KEY (refuse_id) REFERENCES refuse_name(id)
);

CREATE TABLE yield_amount (
                              food_id INT,
                              yield_id INT,
                              amount DOUBLE,
                              date_of_entry TEXT,
                              PRIMARY KEY (food_id, yield_id),
                              FOREIGN KEY (food_id) REFERENCES food_name(id),
                              FOREIGN KEY (yield_id) REFERENCES yield_name(id)
);


CREATE TABLE logged_meal (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             user_name VARCHAR(100),
                             meal_type VARCHAR(50),
                             meal_date DATE
);
CREATE TABLE meal_ingredient (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 meal_id INT,
                                 food_id INT,
                                 quantity_grams DOUBLE,
                                 was_swapped BOOLEAN DEFAULT 0,
                                 FOREIGN KEY (meal_id) REFERENCES logged_meal(id),
                                 FOREIGN KEY (food_id) REFERENCES food_name(id)
);
CREATE TABLE user_profile (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(100) NOT NULL UNIQUE,
                              sex ENUM('Male', 'Female') NOT NULL,
                              date_of_birth DATE NOT NULL,
                              height DOUBLE NOT NULL,
                              weight DOUBLE NOT NULL,
                              unit_system ENUM('Metric', 'Imperial') NOT NULL
);
