IF OBJECT_ID('dbo.Notificacao_Atraso', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Notificacao_Atraso
    (
        id               BIGINT IDENTITY(1,1) NOT NULL,
        agenda_id        INT NOT NULL,
        data_prevista    DATETIME2 NOT NULL,
        email_enviado_em DATETIME2 NOT NULL,

        CONSTRAINT PK_Notificacao_Atraso PRIMARY KEY (id),
        CONSTRAINT FK_Notificacao_Atraso_Agenda FOREIGN KEY (agenda_id) REFERENCES dbo.Agenda(id) ON DELETE CASCADE,
        CONSTRAINT UQ_Notificacao_Atraso_Agenda_Data UNIQUE (agenda_id, data_prevista)
    );
END
GO
