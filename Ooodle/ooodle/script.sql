DROP DATABASE IF EXISTS BDPractica;
CREATE DATABASE IF NOT EXISTS BDPractica;

USE BDPractica;

DROP TABLE IF EXISTS Datos;

CREATE TABLE Datos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE, 
    -- Cambiamos NOT NULL por NULL para que el primer INSERT funcione
    Registro JSON NULL 
);
-- Verificación
SELECT * FROM Datos;
