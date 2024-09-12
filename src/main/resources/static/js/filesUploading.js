function updateFileList(input) {
    const fileList = document.getElementById('file-list');
    fileList.innerHTML = ''; // Очистить список перед обновлением

    const files = input.files;

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const listItem = document.createElement('div');
        listItem.textContent = file.name;
        fileList.appendChild(listItem);
    }

    // Изменить текст в области для загрузки файлов
    const fileDropText = document.querySelector('.file-drop-text');
    if (files.length === 0) {
        fileDropText.textContent = 'Drag and Drop';
    } else {
        fileDropText.textContent = `${files.length} file${files.length > 1 ? 's' : ''} selected`;
    }
}

// Обработчики событий drag и drop
const fileDropArea = document.querySelector('.file-upload-label');

['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
    fileDropArea.addEventListener(eventName, preventDefaults, false);
});

function preventDefaults(e) {
    e.preventDefault();
    e.stopPropagation();
}

['dragenter', 'dragover'].forEach(eventName => {
    fileDropArea.addEventListener(eventName, highlight, false);
});

['dragleave', 'drop'].forEach(eventName => {
    fileDropArea.addEventListener(eventName, unhighlight, false);
});

function highlight(e) {
    fileDropArea.classList.add('highlighted');
}

function unhighlight(e) {
    fileDropArea.classList.remove('highlighted');
}

fileDropArea.addEventListener('drop', handleDrop, false);

function handleDrop(e) {
    const files = e.dataTransfer.files;
    const fileInput = document.getElementById('file-drop');
    fileInput.files = files;
    updateFileList(fileInput);
}

/*<![CDATA[*/
const maxFileSizeBytes = /*[[${@environment.getProperty('spring.servlet.multipart.max-file-size')}]]*/ 50000000;

function checkFileSize(files) {
    const messageDivElement = document.getElementById('message-div-window');
    const submitButton = document.getElementById('submit-button');
    messageDivElement.classList.remove('message-div_hidden-window');

    let fileSizeValid = true;
    let messageText = '';

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (file.size > maxFileSizeBytes) {
            fileSizeValid = false;
            messageText += `Файл "${file.name}" превышает максимально допустимый размер (${maxFileSizeBytes / 1024 / 1024} МБ)\n`;
        }
    }

    if (fileSizeValid) {
        messageDivElement.textContent = 'Все файлы имеют допустимый размер';
        submitButton.disabled = false; // Разрешить кнопку отправки
    } else {
        messageDivElement.textContent = messageText;
        submitButton.disabled = true; // Запретить кнопку отправки
    }
}



//del Modal
