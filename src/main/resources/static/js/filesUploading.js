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
const maxFileSizeBytes = /*[[${@environment.getProperty('spring.servlet.multipart.max-file-size')}]]*/ 500000000;

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
            messageText += `file "${file.name}" exceeds the maximum allowed size (${maxFileSizeBytes / 1024 / 1024} МБ)\n`;
        }
    }

    if (fileSizeValid) {
        messageDivElement.textContent = 'All files are of acceptable size.';
        submitButton.disabled = false; // Разрешить кнопку отправки
    } else {
        messageDivElement.textContent = messageText;
        submitButton.disabled = true; // Запретить кнопку отправки
    }
}


function sortTable(n) {
    var table = document.getElementById("fileTable");
    var thead = table.getElementsByTagName("thead")[0];
    var headers = thead.getElementsByTagName("th");
    var dir = "asc";

    // Remove 'active' class from all headers
    for (var i = 0; i < headers.length; i++) {
        headers[i].classList.remove("active");
    }

    // Add 'active' class to current header
    headers[n].classList.add("active");

    if (headers[n].classList.contains("asc")) {
        dir = "desc";
        headers[n].classList.remove("asc");
        headers[n].classList.add("desc");
    } else {
        headers[n].classList.remove("desc");
        headers[n].classList.add("asc");
    }

    var rows, switching, i, x, y, shouldSwitch, switchcount = 0;
    switching = true;

    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            if (n === 0) {
                x = rows[i].getElementsByTagName("TD")[n].getElementsByTagName("SPAN")[0].innerText.toLowerCase();
                y = rows[i + 1].getElementsByTagName("TD")[n].getElementsByTagName("SPAN")[0].innerText.toLowerCase();
            } else if (n === 2) {
                // Парсинг размера файла с учетом МБ и КБ
                x = parseFloat(rows[i].getElementsByTagName("TD")[n].innerText);
                y = parseFloat(rows[i + 1].getElementsByTagName("TD")[n].innerText);

                // Конвертация в байты для корректного сравнения
                if (rows[i].getElementsByTagName("TD")[n].innerText.includes('KB')) {
                    x *= 1024; // Конвертация КБ в байты
                } else if (rows[i].getElementsByTagName("TD")[n].innerText.includes('MB')) {
                    x *= 1024 * 1024; // Конвертация МБ в байты
                }

                if (rows[i + 1].getElementsByTagName("TD")[n].innerText.includes('KB')) {
                    y *= 1024; // Конвертация КБ в байты
                } else if (rows[i + 1].getElementsByTagName("TD")[n].innerText.includes('MB')) {
                    y *= 1024 * 1024; // Конвертация МБ в байты
                }
            } else if (n === 3) {
                // Обработка даты и времени
                x = parseDateString(rows[i].getElementsByTagName("TD")[n].innerText);
                y = parseDateString(rows[i + 1].getElementsByTagName("TD")[n].innerText);
            }
            else {
                x = rows[i].getElementsByTagName("TD")[n].innerText.toLowerCase();
                y = rows[i + 1].getElementsByTagName("TD")[n].innerText.toLowerCase();
            }

            if (dir == "asc") {
                if (x > y) {
                    shouldSwitch = true;
                    break;
                }
            } else if (dir == "desc") {
                if (x < y) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount ++;
        }
    }
}

// Add event listeners for sorting
document.addEventListener('DOMContentLoaded', function() {
    var headers = document.querySelectorAll('#fileTable th.sortable');
    headers.forEach(function(header) {
        header.addEventListener('click', function() {
            var n = this.getAttribute('data-sort');
            sortTable(parseInt(n));
        });
    });
});

// Helper function to parse date strings, handling different formats
function parseDateString(dateString) {
    // Attempt to parse using different formats
    let parsedDate = new Date(dateString); // Try default Date parsing first

    // If default parsing fails, try parsing with a specific format
    if (isNaN(parsedDate.getTime())) {
        // Example: dd-MM-yyyy HH:mm:ss or dd-MM-yyyy HH:mm
        const parts = dateString.split(/[- :]/);
        if (parts.length >= 5) {
            const day = parseInt(parts[0], 10);
            const month = parseInt(parts[1], 10) - 1; // Month is 0-indexed
            const year = parseInt(parts[2], 10);
            const hour = parseInt(parts[3], 10);
            const minute = parseInt(parts[4], 10);
            const second = parts.length > 5 ? parseInt(parts[5], 10) : 0; // Optional seconds
            parsedDate = new Date(year, month, day, hour, minute, second);
        }
    }

    // If parsing still fails, return an invalid date
    if (isNaN(parsedDate.getTime())) {
        console.warn("Could not parse date:", dateString);
        return new Date(NaN); // Return an invalid date
    }

    return parsedDate;
}