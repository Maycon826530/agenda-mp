IF OBJECT_ID('dbo.recuperar_senha', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.recuperar_senha
    (
        id            BIGINT         IDENTITY(1,1) NOT NULL,
        email         VARCHAR(254)   NOT NULL,
        codigo        VARCHAR(6)     NOT NULL,
        gerado_em     DATETIME2      NOT NULL,
        exepira_em    DATETIME2      NOT NULL,
        status_codigo BIT            NOT NULL,

        CONSTRAINT PK_recuperar_senha PRIMARY KEY (id)
    );

    CREATE INDEX IX_recuperar_senha_email_codigo_status
        ON dbo.recuperar_senha (email, codigo, status_codigo);
END
GO
