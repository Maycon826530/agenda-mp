CREATE TABLE Usuarios
(
   id INT IDENTITY,
   nome VARCHAR(100) NOT NULL,
   data_nascimento DATE NOT NULL,
   comorbidade VARCHAR(2000) NULL,
   email VARCHAR(100) NOT NULL,
   senha VARCHAR(100) NOT NULL,
   tipo_notificacao VARCHAR(20) NOT NULL DEFAULT 'sistema',
   PRIMARY KEY (id)
);
GO
 
CREATE TABLE Agenda
(
   id INT IDENTITY,
   nome VARCHAR(100) NOT NULL,
   dosagem VARCHAR(100) NOT NULL,
   horario TIME NOT NULL,
   data_inicio SMALLDATETIME NOT NULL,
   data_fim SMALLDATETIME NOT NULL,
   observacoes VARCHAR(100) NOT NULL,
   usuarios_id INT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (usuarios_id) REFERENCES Usuarios (id)
);
GO
 
CREATE TABLE Horario
(
   id INT IDENTITY,
   data SMALLDATETIME NOT NULL,
   hora TIME NOT NULL,
   status VARCHAR(20) NOT NULL,
   PRIMARY KEY (id)
);
GO
 
CREATE TABLE Medicamento
(
   id INT IDENTITY,
   nome VARCHAR(100) NOT NULL,
   descricao VARCHAR(100) NOT NULL,
   tipo VARCHAR(100) NOT NULL,
   complemento VARCHAR(50) NULL,
   data_cadastro SMALLDATETIME NOT NULL,
   status_medicamento VARCHAR(20) NOT NULL,
   agenda_id INT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (agenda_id) REFERENCES Agenda (id)
);
GO
 
CREATE TABLE Historico
(
   id INT IDENTITY,
   nome VARCHAR(100) NOT NULL,
   dosagem VARCHAR(100) NOT NULL,
   observacoes VARCHAR(100) NOT NULL,
   horario TIME NOT NULL,
   data_confirmacao DATETIME2 NULL,
   motivo_ignorado VARCHAR(200) NULL,
   data_hora_ignorado DATETIME2 NULL,
   status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
   medicamento_id INT NULL,
   agenda_id INT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (medicamento_id) REFERENCES Medicamento (id),
   FOREIGN KEY (agenda_id) REFERENCES Agenda (id)
);
GO

CREATE TABLE Lembrete
(
   id INT IDENTITY,
   titulo VARCHAR(120) NOT NULL,
   descricao VARCHAR(500) NULL,
   data DATE NOT NULL,
   horario TIME NOT NULL,
   usuarios_id INT NOT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (usuarios_id) REFERENCES Usuarios (id)
);
GO

CREATE TABLE recuperar_senha
( 
   id				   BIGINT			IDENTITY,
   email			   VARCHAR(254)	NOT NULL, -- username
   codigo			VARCHAR(6)		NOT NULL,
   gerado_em		DATETIME2		NOT NULL DEFAULT GETDATE(),
   exepira_em		DATETIME2		NOT NULL,
   status_codigo	BIT				NOT NULL DEFAULT 1, -- 1 = ATIVO ou 0 = INATIVO

   PRIMARY KEY (id)
);
GO

CREATE INDEX IX_Lembrete_Usuario_Data
ON Lembrete (usuarios_id, data, horario);
GO
 
SELECT * FROM Usuarios
 
SELECT * FROM Medicamento

SELECT * FROM Historico
