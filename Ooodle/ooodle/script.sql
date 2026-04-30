DROP DATABASE IF EXISTS BDPractica;
CREATE DATABASE IF NOT EXISTS BDPractica;
USE BDPractica;

DROP TABLE IF EXISTS Estadisticas;
DROP TABLE IF EXISTS Usuario;

CREATE TABLE Usuario (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nickname VARCHAR(100) NOT NULL, 
    UltimoJuego DATE  
);

CREATE TABLE Estadisticas (
    idUsuario INT PRIMARY KEY, 
    Racha_Actual INT DEFAULT 0,
    Racha_Max INT DEFAULT 0,
    P_Jugadas INT DEFAULT 0,
    P_Ganadas INT DEFAULT 0,
    
    FOREIGN KEY (idUsuario) REFERENCES Usuario(Id) ON DELETE CASCADE
);

-- Verificación
SELECT * FROM Usuario;
