CREATE DATABASE IF NOT EXISTS weather;

USE weather;

CREATE TABLE IF NOT EXISTS location (
    loc_id VARCHAR(100) PRIMARY KEY,
    loc_name VARCHAR(100),
    loc_latitude DOUBLE,
    loc_longitude DOUBLE
);