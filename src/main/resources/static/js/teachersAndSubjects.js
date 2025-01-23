document.getElementById('teacherSearch').addEventListener('input', function(e) {
    const input = e.target;
    const datalist = document.getElementById('teachersList');
    const options = datalist.getElementsByTagName('option');

    // Найти выбранный email в списке опций
    for(let option of options) {
        if(option.value === input.value) {
            // Когда нашли совпадение, сохраняем ID в скрытом поле
            document.getElementById('selectedTeacherId').value = option.getAttribute('data-id');
            break;
        }
    }
});


// Обработчик для формы удаления учителя
document.querySelectorAll('.delete-teacher-form form').forEach(form => {
    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const modalId = 'confirmDeleteModalTeacher';
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.id = modalId;
        modal.innerHTML = `
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirmation of deletion</h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to remove this teacher?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Нет</button>
                    <button type="button" class="btn btn-primary" id="confirmDeleteYes">Да</button>
                </div>
            </div>
        </div>
        `;

        document.body.appendChild(modal);
        $(`#${modalId}`).modal('show');

        document.getElementById('confirmDeleteYes').addEventListener('click', function () {
            $(`#${modalId}`).modal('hide');
            form.submit();
        });

        $(`#${modalId}`).on('hidden.bs.modal', function () {
            $(this).remove();
        });
    });
});
