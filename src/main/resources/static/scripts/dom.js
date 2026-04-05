export function showAlert(message, type = 'success') {
    const alert = document.createElement('div');
    alert.id = 'main-alert';
    alert.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    alert.style.zIndex = '2000';
    alert.role = 'alert';
    alert.innerHTML = `
        <div class="d-flex align-items-center">
            <div id="alert-message">${message.replace(/\n/g, '<br>')}</div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(alert);
    setTimeout(() => {
        alert.classList.remove('show');
        setTimeout(() => alert.remove(), 500);
    }, 5000);
}

export function renderTabelaContas(contas, onEditar, onExcluir) {
    const tabelaBody = document.querySelector('#tabela-contas tbody');
    tabelaBody.innerHTML = '';

    if (contas.length === 0) {
        tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center">Nenhuma conta cadastrada.</td></tr>';
        return;
    }

    contas.forEach(conta => {
        const tr = document.createElement('tr');
        const badgeClass = conta.status === 'ATIVA' ? 'bg-success' : 'bg-danger';

        tr.innerHTML = `
            <td>${conta.id}</td>
            <td><strong>${conta.titular}</strong><br><small class="text-muted">${conta.documento}</small></td>
            <td>${conta.agencia} / ${conta.numeroConta}</td>
            <td>R$ ${conta.saldo.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
            <td>
                <span class="badge ${badgeClass}" style="cursor:pointer" title="Clique para mudar o status">
                    ${conta.status}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-warning btn-editar">Editar</button>
                <button class="btn btn-sm btn-outline-danger btn-excluir">Excluir</button>
            </td>
        `;

        tr.querySelector('.btn-editar').onclick = () => onEditar(conta);
        tr.querySelector('.btn-excluir').onclick = () => onExcluir(conta);

        tabelaBody.appendChild(tr);
    });
}

