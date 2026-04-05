const API_URL = 'http://localhost:8080/banco-api/contas';

async function tratarResposta(resp) {
    if (resp.ok) return resp.status === 204 ? null : await resp.json();

    const errorData = await resp.json();
    let mensagem = errorData.mensagem || errorData.erro || 'Erro na operação';

    if (resp.status === 422 && errorData.campos) {
        const detalhes = errorData.campos.map(c => `- ${c.campo}: ${c.mensagem}`).join('\n');
        mensagem = `Erro de Validação:\n${detalhes}`;
    }

    throw new Error(mensagem);
}

export async function listarContas() {
    const resp = await fetch(API_URL);
    return await tratarResposta(resp);
}

export async function criarConta(dados) {
    const resp = await fetch(API_URL, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(dados)
    });
    return await tratarResposta(resp);
}

export async function atualizarConta(id, nome, saldo) {
    const resp = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            titular: nome,
            saldo: saldo
        })
    });
    return await tratarResposta(resp);
}

export async function excluirConta(id) {
    const resp = await fetch(
        `${API_URL}/${id}`,
        {method: 'DELETE'}
    );
    return await tratarResposta(resp);
}