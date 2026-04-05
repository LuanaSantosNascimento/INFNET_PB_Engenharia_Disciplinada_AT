import { listarContas, criarConta, atualizarConta, excluirConta } from './api.js';
import { showAlert, renderTabelaContas } from './dom.js';

export function setupEventHandlers() {
    const formCriar = document.getElementById('form-criar');
    const modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
    const formEditar = document.getElementById('form-editar');
    const inputEditarId = document.getElementById('editar-id');
    const inputEditarNome = document.getElementById('editar-nome');
    const inputEditarSaldo = document.getElementById('editar-saldo');
    const modalExcluir = new bootstrap.Modal(document.getElementById('modalExcluir'));
    const inputExcluirId = document.getElementById('excluir-id');
    const btnConfirmarExcluir = document.getElementById('btn-confirmar-excluir');

    async function atualizarTabela() {
        try {
            const contas = await listarContas();
            renderTabelaContas(contas, abrirEditar, abrirExcluir);
        } catch (e) {
            showAlert(e.message, 'danger');
        }
    }

    formCriar.addEventListener('submit', async (e) => {
        e.preventDefault();

        const novaConta = {
            titular: document.getElementById('nome').value.trim(),
            documento: document.getElementById('documento').value.trim(),
            tipoConta: document.getElementById('tipoConta').value
        };

        try {
            await criarConta(novaConta);
            formCriar.reset();
            showAlert('Conta criada com sucesso!');
            await atualizarTabela();
        } catch (e) {
            showAlert(e.message, 'danger');
        }
    });

    function abrirEditar(conta) {
        document.getElementById('editar-id').value = conta.id;
        document.getElementById('editar-nome').value = conta.titular;
        document.getElementById('editar-saldo').value = conta.saldo;
        modalEditar.show();
    }

    formEditar.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = inputEditarId.value;
        const nome = inputEditarNome.value.trim();
        const saldo = parseFloat(inputEditarSaldo.value);
        if (!nome || isNaN(saldo) || saldo < 0) {
            showAlert('Preencha os campos corretamente.', 'warning');
            return;
        }
        try {
            await atualizarConta(id, nome, saldo);
            modalEditar.hide();
            showAlert('Conta atualizada com sucesso!');
            atualizarTabela();
        } catch (e) {
            showAlert(e.message, 'danger');
        }
    });

    function abrirExcluir(conta) {
        inputExcluirId.value = conta.id;
        modalExcluir.show();
    }

    btnConfirmarExcluir.addEventListener('click', async () => {
        const id = inputExcluirId.value;
        try {
            await excluirConta(id);
            modalExcluir.hide();
            showAlert('Conta excluída com sucesso!');
            await atualizarTabela();
        } catch (e) {
            showAlert(e.message, 'danger');
        }
    });

    // Inicialização
    atualizarTabela();
}



