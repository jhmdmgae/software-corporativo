CREATE TABLE TB_USUARIO (ID BIGINT NOT NULL, TXT_CPF VARCHAR(14) NOT NULL UNIQUE, DT_NASCIMENTO DATE, TXT_EMAIL VARCHAR(50) NOT NULL, TXT_LOGIN VARCHAR(50) NOT NULL, TXT_NOME VARCHAR(255) NOT NULL, TXT_SENHA VARCHAR(20) NOT NULL, PRIMARY KEY (ID))
CREATE TABLE TB_GERADOR_ID (NM_SEQUENCIA VARCHAR(50) NOT NULL, ID_SEQUENCIA DECIMAL(38), PRIMARY KEY (NM_SEQUENCIA))
INSERT INTO TB_GERADOR_ID(NM_SEQUENCIA, ID_SEQUENCIA) values ('TB_USUARIO_ID', 0)