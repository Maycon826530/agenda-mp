-- Executar uma vez no SQL Server antes da versao com roles.
ALTER TABLE Usuarios ADD role VARCHAR(20) NULL;
GO

UPDATE Usuarios SET role = 'USER' WHERE role IS NULL;
GO

ALTER TABLE Usuarios ALTER COLUMN role VARCHAR(20) NOT NULL;
GO

ALTER TABLE Usuarios ADD CONSTRAINT DF_Usuarios_role DEFAULT 'USER' FOR role;
GO
