drop database if exists BDPractica;
CREATE DATABASE IF NOT EXISTS BDPractica;

Use BDPractica;

DROP TABLE IF EXISTS Datos;

create table Datos(
	id Int auto_increment primary key,
    nombre varchar(100),
    Registro TEXT NOT NULL -- se guarda el archivo json
)


