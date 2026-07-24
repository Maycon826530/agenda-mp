IF OBJECT_ID('dbo.Lembrete', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Lembrete (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        titulo VARCHAR(120) NOT NULL,
        descricao VARCHAR(500) NULL,
        data DATE NOT NULL,
        horario TIME NOT NULL,
        usuarios_id INT NOT NULL,
        CONSTRAINT FK_Lembrete_Usuarios
            FOREIGN KEY (usuarios_id) REFERENCES dbo.Usuarios(id)
    );

    CREATE INDEX IX_Lembrete_Usuario_Data
        ON dbo.Lembrete (usuarios_id, data, horario);
END;
