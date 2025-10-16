-- Script para testar a conexão e verificar dados
USE bd_tccamp;

-- Verificar se a tabela Usuarios existe e tem dados
SELECT COUNT(*) as total_usuarios FROM Usuarios;

-- Listar todos os usuários
SELECT id, nome, email, nivelAcesso, statusUsuario, dataCadastro FROM Usuarios;

-- Testar login com dados existentes
SELECT * FROM Usuarios WHERE email = 'fulano@email.com.br' AND senha = 'MTIzNDU2Nzg=';

-- Verificar estrutura da tabela
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Usuarios'
ORDER BY ORDINAL_POSITION;