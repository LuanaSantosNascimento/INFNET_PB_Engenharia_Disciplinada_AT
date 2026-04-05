-- Inicialização do banco de dados
INSERT INTO conta (titular, documento, numero_conta, agencia, saldo, status, tipo_conta, data_criacao)
VALUES ('João Silva', '12345678901', '10001-1', '0001', 1000.00, 'ATIVA', 'CORRENTE', NOW());

INSERT INTO conta (titular, documento, numero_conta, agencia, saldo, status, tipo_conta, data_criacao)
VALUES ('Maria Santos', '23456789012', '20002-2', '0001', 2500.50, 'ATIVA', 'POUPANCA', NOW());

INSERT INTO conta (titular, documento, numero_conta, agencia, saldo, status, tipo_conta, data_criacao)
VALUES ('Pedro Oliveira', '34567890123', '30003-3', '0001', 0.00, 'ATIVA', 'CORRENTE', NOW());

INSERT INTO conta (titular, documento, numero_conta, agencia, saldo, status, tipo_conta, data_criacao)
VALUES ('Ana Costa', '45678901234', '40004-4', '0001', 0.00, 'ATIVA', 'CORRENTE', NOW());

INSERT INTO conta (titular, documento, numero_conta, agencia, saldo, status, tipo_conta, data_criacao)
VALUES ('Carlos Lima', '56789012345', '50005-5', '0001', 0.00, 'BLOQUEADA', 'POUPANCA', NOW());